package com.nlizzard.horizonhub.ai.context;

import com.nlizzard.horizonhub.entity.enums.ArticleStatusEnum;
import com.nlizzard.horizonhub.entity.enums.PageSize;
import com.nlizzard.horizonhub.entity.pojo.ForumArticle;
import com.nlizzard.horizonhub.entity.pojo.ForumBoard;
import com.nlizzard.horizonhub.entity.query.ForumArticleQuery;
import com.nlizzard.horizonhub.entity.query.basequery.SimplePage;
import com.nlizzard.horizonhub.service.ForumArticleService;
import com.nlizzard.horizonhub.service.ForumBoardService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 论坛上下文检索：把论坛内容（板块、热门帖、相关帖）组装成喂给 LLM 的上下文文本。
 * <p>
 * AI 不直接查库，而是由本服务预取相关数据作为 prompt 上下文（检索增强思路），
 * 让 LLM 基于真实论坛数据回答，避免幻觉。
 */
@Service
public class ForumContextService {

    /** 每次喂给 LLM 的帖子条数上限（控制 token 消耗） */
    private static final int MAX_ARTICLES = 8;

    @Resource
    private ForumArticleService forumArticleService;

    @Resource
    private ForumBoardService forumBoardService;

    /**
     * 组装系统提示词：角色设定 + 板块结构 + 指引。
     */
    public String buildSystemPrompt() {
        StringBuilder sb = new StringBuilder();
        sb.append("你是 HorizonHub 论坛的智能助手「小H」，负责帮助用户了解论坛、寻找感兴趣的帖子。")
                .append("请基于下方提供的论坛真实数据回答，不要编造不存在的帖子或板块。\n");
        sb.append("回答要简洁友好，使用中文。如果用户在找帖子，请推荐相关帖子并简述内容。\n\n");

        // 板块结构
        sb.append("【当前论坛板块】\n");
        List<ForumBoard> boards = forumBoardService.getBoardTree(null);
        if (boards != null && !boards.isEmpty()) {
            for (ForumBoard b : boards) {
                sb.append("- ").append(b.getBoardName());
                if (StringUtils.isNotBlank(b.getBoardDesc())) {
                    sb.append("（").append(b.getBoardDesc()).append("）");
                }
                if (b.getChildren() != null && !b.getChildren().isEmpty()) {
                    sb.append("，含子板块：");
                    for (int i = 0; i < b.getChildren().size(); i++) {
                        if (i > 0) sb.append("、");
                        sb.append(b.getChildren().get(i).getBoardName());
                    }
                }
                sb.append("\n");
            }
        } else {
            sb.append("（暂无板块）\n");
        }
        sb.append("\n当用户问的帖子不在你看到的数据中时，建议用户去搜索页用关键词搜索。\n");
        return sb.toString();
    }

    /**
     * 按用户提问组装相关帖子上下文：
     * 有显著关键词时按关键词搜，否则取热门帖。
     */
    public String buildUserContext(String userMessage) {
        StringBuilder sb = new StringBuilder();
        List<ForumArticle> articles;
        String keyword = extractKeyword(userMessage);
        if (keyword != null) {
            articles = searchArticles(keyword, MAX_ARTICLES);
            sb.append("【与「").append(keyword).append("」相关的最新帖子】\n");
        } else {
            articles = hotArticles(MAX_ARTICLES);
            sb.append("【当前热门帖子】\n");
        }
        if (articles == null || articles.isEmpty()) {
            sb.append("（暂无相关帖子）\n");
            return sb.toString();
        }
        for (ForumArticle a : articles) {
            sb.append("- 《").append(a.getTitle()).append("》");
            if (StringUtils.isNotBlank(a.getPBoardName())) {
                sb.append(" [板块:").append(a.getPBoardName());
                if (StringUtils.isNotBlank(a.getBoardName())) {
                    sb.append("/").append(a.getBoardName());
                }
                sb.append("]");
            }
            if (StringUtils.isNotBlank(a.getSummary())) {
                // 摘要截断，控制长度
                String s = a.getSummary();
                sb.append(" 摘要：").append(s.length() > 60 ? s.substring(0, 60) + "..." : s);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * 从用户消息中提取搜索关键词（简单启发式：取 2 字以上的中文/英文片段）。
     * 复杂意图理解交给 LLM，这里只做粗筛以决定要不要带搜索结果。
     */
    private String extractKeyword(String message) {
        if (message == null || message.isBlank()) {
            return null;
        }
        String trimmed = message.trim();
        // 过滤掉过于宽泛的提问，避免无意义搜索
        if (trimmed.length() < 2) {
            return null;
        }
        // 去掉常见疑问词，取剩余主体作为关键词（粗略）
        String keyword = trimmed.replaceAll("(?i)(有没有|推荐|帮找|帮我找|搜索|查找|关于|相关|的|吗|呢|吧|？|\\?)", "").trim();
        return keyword.length() >= 2 ? keyword : null;
    }

    private List<ForumArticle> searchArticles(String keyword, int limit) {
        ForumArticleQuery query = new ForumArticleQuery();
        query.setTitleFuzzy(keyword);
        query.setStatus(ArticleStatusEnum.AUDIT.getStatus());
        return queryArticles(query, limit);
    }

    private List<ForumArticle> hotArticles(int limit) {
        ForumArticleQuery query = new ForumArticleQuery();
        query.setStatus(ArticleStatusEnum.AUDIT.getStatus());
        // 热榜排序（白名单允许，见 BaseQuery.setOrderBy）
        query.setOrderBy("good_count desc,read_count desc");
        return queryArticles(query, limit);
    }

    private List<ForumArticle> queryArticles(ForumArticleQuery query, int limit) {
        query.setPageNo(1);
        query.setPageSize(PageSize.SIZE15.getSize());
        // 限制取前 limit 条
        List<ForumArticle> list = forumArticleService.findListByParam(query);
        return list != null && list.size() > limit ? list.subList(0, limit) : list;
    }
}
