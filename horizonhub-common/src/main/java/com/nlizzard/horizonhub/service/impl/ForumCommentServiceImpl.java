package com.nlizzard.horizonhub.service.impl;

import com.nlizzard.horizonhub.entity.enums.CommentStatusEnum;
import com.nlizzard.horizonhub.entity.enums.CommentTopTypeEnum;
import com.nlizzard.horizonhub.entity.enums.PageSize;
import com.nlizzard.horizonhub.entity.enums.ResponseCodeEnum;
import com.nlizzard.horizonhub.entity.pojo.ForumArticle;
import com.nlizzard.horizonhub.entity.pojo.ForumComment;
import com.nlizzard.horizonhub.entity.query.ForumArticleQuery;
import com.nlizzard.horizonhub.entity.query.ForumCommentQuery;
import com.nlizzard.horizonhub.entity.query.basequery.SimplePage;
import com.nlizzard.horizonhub.entity.vo.PaginationResultVO;
import com.nlizzard.horizonhub.exception.BusinessException;
import com.nlizzard.horizonhub.mappers.ForumArticleMapper;
import com.nlizzard.horizonhub.mappers.ForumCommentMapper;
import com.nlizzard.horizonhub.service.ForumCommentService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description:评论ServiceImpl
 * @author:nlizzard
 * @date:2026/03/08
 */
@Service("forumCommentService")
public class ForumCommentServiceImpl implements ForumCommentService {

    @Resource
    private ForumCommentMapper<ForumComment, ForumCommentQuery> forumCommentMapper;

    @Resource
    private ForumArticleMapper<ForumArticle, ForumArticleQuery> forumArticleMapper;

    /**
     * 根据条件查询列表
     */
    @Override
    public List<ForumComment> findListByParam(ForumCommentQuery query) {
        // 拿到一级评论
        List<ForumComment> list = this.forumCommentMapper.selectList(query);
        //获取二级评论
        if (query.getLoadChildren() != null && query.getLoadChildren()) {
            // 复制部分一级评论查询条件
            ForumCommentQuery subQuery = new ForumCommentQuery();
            subQuery.setQueryIsLike(query.getQueryIsLike());
            subQuery.setCurrentUserId(query.getCurrentUserId());
            subQuery.setArticleId(query.getArticleId());
            subQuery.setLoadChildren(query.getLoadChildren());
            subQuery.setStatus(CommentStatusEnum.AUDIT.getStatus());
            // 根据当前分页结果中的一级评论查询二级评论
            List<Integer> parentCommentIdList = list.stream().map(ForumComment::getCommentId).collect(Collectors.toList());
            subQuery.setParentCommentIdList(parentCommentIdList);
            List<ForumComment> subCommentList = this.forumCommentMapper.selectList(subQuery);
            Map<Integer, List<ForumComment>> tempMap = subCommentList.stream().collect(Collectors.groupingBy(ForumComment::getPCommentId));
            list.forEach(item -> {
                item.setChildren(tempMap.get(item.getCommentId()));
            });
        }
        return list;
    }

    /**
     * 根据条件查询数量
     */
    @Override
    public Integer findCountByParam(ForumCommentQuery query) {
        return this.forumCommentMapper.selectCount(query);
    }

    /**
     * 分页查询
     */
    @Override
    public PaginationResultVO<ForumComment> findListByPage(ForumCommentQuery query) {
        Integer count = this.findCountByParam(query);
        Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
        SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
        query.setSimplePage(page);
        List<ForumComment> list = this.findListByParam(query);
        return new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
    }

    /**
     * 新增
     */
    @Override
    public Integer add(ForumComment bean) {
        return this.forumCommentMapper.insert(bean);
    }

    /**
     * 批量新增
     */
    @Override
    public Integer addBatch(List<ForumComment> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.forumCommentMapper.insertBatch(listBean);
    }

    /**
     * 批量新增或修改
     */
    @Override
    public Integer addOrUpdateBatch(List<ForumComment> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.forumCommentMapper.insertOrUpdateBatch(listBean);
    }

    /**
     * 根据CommentId查询
     */
    @Override
    public ForumComment getForumCommentByCommentId(Integer commentId) {
        return this.forumCommentMapper.selectByCommentId(commentId);
    }

    /**
     * 根据CommentId更新
     */
    @Override
    public Integer updateForumCommentByCommentId(ForumComment bean, Integer commentId) {
        return this.forumCommentMapper.updateByCommentId(bean, commentId);
    }

    /**
     * 根据CommentId删除
     */
    @Override
    public Integer deleteForumCommentByCommentId(Integer commentId) {
        return this.forumCommentMapper.deleteByCommentId(commentId);
    }

    /**
     * 置顶/取消置顶评论
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeTopType(String userId, Integer commentId, Integer topType) {
        // 判断是否置顶参数合法性
        CommentTopTypeEnum commentTopTypeEnum = CommentTopTypeEnum.getByType(topType);
        if (commentTopTypeEnum == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        // 检验评论是否存在，是否是一级评论（只能置顶一级评论）
        ForumComment forumComment = forumCommentMapper.selectByCommentId(commentId);
        if (forumComment == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_404.getCode(), "评论不存在");
        }
        if (forumComment.getPCommentId() != 0) {
            throw new BusinessException(ResponseCodeEnum.CODE_600.getCode(), "只能置顶一级评论");
        }
        // 检验文章是否存在,是否是作者，是否具备置顶权限
        ForumArticle forumArticle = forumArticleMapper.selectByArticleId(forumComment.getArticleId());
        if (forumArticle == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_404.getCode(), "文章不存在");
        }
        if (!forumArticle.getUserId().equals(userId)) {
            throw new BusinessException("没有权限操作");
        }
        // 判断评论当前置顶状态与目标置顶状态是否一致，如果一致则无需操作
        if (forumComment.getTopType().equals(topType)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600.getCode(), "评论已处于当前置顶状态，无需重复操作");
        }

        // 置顶评论
        ForumComment updateInfo = new ForumComment();
        updateInfo.setTopType(topType);
        forumCommentMapper.updateByCommentId(updateInfo, forumComment.getCommentId());
    }
}