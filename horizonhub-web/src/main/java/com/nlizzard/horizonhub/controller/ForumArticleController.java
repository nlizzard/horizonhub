package com.nlizzard.horizonhub.controller;

import com.nlizzard.horizonhub.annotation.GlobalInterceptor;
import com.nlizzard.horizonhub.annotation.VerifyParam;
import com.nlizzard.horizonhub.basecontroller.BaseController;
import com.nlizzard.horizonhub.entity.dto.SessionWebUserDto;
import com.nlizzard.horizonhub.entity.enums.ArticleOrderTypeEnum;
import com.nlizzard.horizonhub.entity.enums.ArticleStatusEnum;
import com.nlizzard.horizonhub.entity.enums.OperRecordOpTypeEnum;
import com.nlizzard.horizonhub.entity.enums.ResponseCodeEnum;
import com.nlizzard.horizonhub.entity.pojo.ForumArticle;
import com.nlizzard.horizonhub.entity.pojo.ForumArticleAttachment;
import com.nlizzard.horizonhub.entity.pojo.LikeRecord;
import com.nlizzard.horizonhub.entity.query.ForumArticleAttachmentQuery;
import com.nlizzard.horizonhub.entity.query.ForumArticleQuery;
import com.nlizzard.horizonhub.entity.vo.PaginationResultVO;
import com.nlizzard.horizonhub.entity.vo.ResponseVO;
import com.nlizzard.horizonhub.entity.vo.web.FormArticleDetailVO;
import com.nlizzard.horizonhub.entity.vo.web.ForumArticleAttachmentVo;
import com.nlizzard.horizonhub.entity.vo.web.ForumArticleVO;
import com.nlizzard.horizonhub.exception.BusinessException;
import com.nlizzard.horizonhub.service.ForumArticleAttachmentService;
import com.nlizzard.horizonhub.service.ForumArticleService;
import com.nlizzard.horizonhub.service.LikeRecordService;
import com.nlizzard.horizonhub.utils.CopyTools;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/forum")
public class ForumArticleController extends BaseController {

    @Resource
    private ForumArticleService forumArticleService;

    @Resource
    private ForumArticleAttachmentService forumArticleAttachmentService;

    @Resource
    private LikeRecordService likeRecordService;

    /**
     * 加载文章列表（首页）
     *
     * @param session   session
     * @param boardId   文章板块ID
     * @param pBoardId  文章父级板块ID
     * @param orderType 排序类型 0:默认排序 1:最新发布 2:最热
     * @param pageNo    页码
     * @return
     */
    @RequestMapping("/loadArticle")
    public ResponseVO<PaginationResultVO<ForumArticleVO>> loadArticle(HttpSession session, Integer boardId, Integer pBoardId, Integer orderType, Integer pageNo) {
        ForumArticleQuery articleQuery = new ForumArticleQuery();
        // 如果boardId为0或null，则查询所有板块的文章
        articleQuery.setBoardId(boardId == null || boardId == 0 ? null : boardId);
        articleQuery.setpBoardId(pBoardId);
        articleQuery.setPageNo(pageNo);

        SessionWebUserDto userDto = getUserInfoFromSession(session);
        if (userDto != null) {
            // 已登录用户可以看到自己的文章和审核通过的文章
            articleQuery.setCurrentUserId(userDto.getUserId());
        } else {
            // 未登录用户只能看到审核通过的文章
            articleQuery.setStatus(ArticleStatusEnum.AUDIT.getStatus());
        }
        // 设置排序方式 根据orderType参数设置排序方式，默认为最热
        ArticleOrderTypeEnum orderTypeEnum = ArticleOrderTypeEnum.getByType(orderType);
        orderTypeEnum = orderTypeEnum == null ? ArticleOrderTypeEnum.HOT : orderTypeEnum;
        articleQuery.setOrderBy(orderTypeEnum.getOrderSql());

        PaginationResultVO<ForumArticle> resultVO = forumArticleService.findListByPage(articleQuery);
        // 转换为 VO 对象并返回
        return getSuccessResponseVO(convert2PaginationVO(resultVO, ForumArticleVO.class));
    }

    /**
     * 获取文章详情
     *
     * @param session   session
     * @param articleId 文章 ID
     * @return
     */
    @RequestMapping("/getArticleDetail")
    public ResponseVO<FormArticleDetailVO> getArticleDetail(HttpSession session, String articleId) {
        SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);

        // 根据文章ID查询文章详情
        ForumArticle forumArticle = forumArticleService.readArticle(articleId);

        // 文章不存在，或者文章未审核且当前用户不是作者或管理员，或者文章已删除，则返回404
        if (forumArticle == null
                || (ArticleStatusEnum.NO_AUDIT.getStatus().equals(forumArticle.getStatus()) && (sessionWebUserDto == null || !sessionWebUserDto.getUserId().equals(forumArticle.getUserId()) && !sessionWebUserDto.getAdmin()))
                || ArticleStatusEnum.DEL.getStatus().equals(forumArticle.getStatus())) {
            throw new BusinessException(ResponseCodeEnum.CODE_404);
        }
        // 组装文章详情VO,如果文章有附件，则查询附件信息并设置到VO中，如果用户已登录，则查询用户是否点赞过该文章并设置到VO中
        FormArticleDetailVO detailVO = new FormArticleDetailVO();
        detailVO.setForumArticle(CopyTools.copy(forumArticle, ForumArticleVO.class));
        // 如果文章有附件，则查询附件信息并设置到VO中
        if (forumArticle.getAttachmentType() == 1) {
            ForumArticleAttachmentQuery articleAttachmentQuery = new ForumArticleAttachmentQuery();
            articleAttachmentQuery.setArticleId(forumArticle.getArticleId());
            List<ForumArticleAttachment> forumArticleAttachmentList = forumArticleAttachmentService.findListByParam(articleAttachmentQuery);
            if (!forumArticleAttachmentList.isEmpty()) {
                detailVO.setAttachment(CopyTools.copy(forumArticleAttachmentList.get(0), ForumArticleAttachmentVo.class));
            }
        }
        // 如果用户已登录，则查询用户是否点赞过该文章并设置到VO中
        if (sessionWebUserDto != null) {
            LikeRecord like = likeRecordService.getUserOperRecordByObjectIdAndUserIdAndOpType(articleId, sessionWebUserDto.getUserId(), OperRecordOpTypeEnum.ARTICLE_LIKE.getType());
            if (like != null) {
                detailVO.setHaveLike(true);
            }
        }
        return getSuccessResponseVO(detailVO);
    }

    /**
     * 文章点赞/取消点赞
     *
     * @param session
     * @param articleId 文章 ID
     * @return
     */
    @RequestMapping("/doLike")
    @GlobalInterceptor(checkLogin = true, checkParams = true)
    public ResponseVO<Void> doLike(HttpSession session, @VerifyParam(required = true) String articleId) {
        SessionWebUserDto userDto = getUserInfoFromSession(session);
        likeRecordService.doLike(articleId, userDto.getUserId(), userDto.getNickName(), OperRecordOpTypeEnum.ARTICLE_LIKE);
        return getSuccessResponseVO(null);
    }
}
