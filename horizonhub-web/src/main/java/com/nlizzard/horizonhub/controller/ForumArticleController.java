package com.nlizzard.horizonhub.controller;

import com.nlizzard.horizonhub.annotation.GlobalInterceptor;
import com.nlizzard.horizonhub.annotation.VerifyParam;
import com.nlizzard.horizonhub.basecontroller.BaseController;
import com.nlizzard.horizonhub.constants.Constants;
import com.nlizzard.horizonhub.entity.config.WebConfig;
import com.nlizzard.horizonhub.entity.dto.SessionWebUserDto;
import com.nlizzard.horizonhub.entity.enums.ArticleOrderTypeEnum;
import com.nlizzard.horizonhub.entity.enums.ArticleStatusEnum;
import com.nlizzard.horizonhub.entity.enums.OperRecordOpTypeEnum;
import com.nlizzard.horizonhub.entity.enums.ResponseCodeEnum;
import com.nlizzard.horizonhub.entity.pojo.*;
import com.nlizzard.horizonhub.entity.query.ForumArticleAttachmentQuery;
import com.nlizzard.horizonhub.entity.query.ForumArticleQuery;
import com.nlizzard.horizonhub.entity.vo.PaginationResultVO;
import com.nlizzard.horizonhub.entity.vo.ResponseVO;
import com.nlizzard.horizonhub.entity.vo.web.FormArticleDetailVO;
import com.nlizzard.horizonhub.entity.vo.web.ForumArticleAttachmentVo;
import com.nlizzard.horizonhub.entity.vo.web.ForumArticleVO;
import com.nlizzard.horizonhub.exception.BusinessException;
import com.nlizzard.horizonhub.service.*;
import com.nlizzard.horizonhub.utils.CopyTools;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/forum")
public class ForumArticleController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ForumArticleController.class);

    @Resource
    private ForumArticleService forumArticleService;

    @Resource
    private ForumArticleAttachmentService forumArticleAttachmentService;

    @Resource
    private LikeRecordService likeRecordService;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private ForumArticleAttachmentDownloadService forumArticleAttachmentDownloadService;
    @Autowired
    private WebConfig webConfig;

    /**
     * 加载文章列表（首页）
     *
     * @param session   session
     * @param boardId   文章板块 ID
     * @param pBoardId  文章父级板块 ID
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

    /**
     * 获取用户下载信息（积分和是否已下载过文件）
     *
     * @param session
     * @param fileId  附件文件 ID
     * @return
     */
    @RequestMapping("/getUserDownloadInfo")
    @GlobalInterceptor(checkLogin = true, checkParams = true)
    public ResponseVO<Map<String, Object>> getUserDownloadInfo(HttpSession session,
                                                               @VerifyParam(required = true) String fileId) {
        Map<String, Object> result = new HashMap<>();
        // 取用户当前积分
        UserInfo userInfo = userInfoService.getUserInfoByUserId(getUserInfoFromSession(session).getUserId());
        result.put("userIntegral", userInfo.getCurrentIntegral());
        // 查询用户是否已下载过该附件
        ForumArticleAttachmentDownload attachmentDownload = forumArticleAttachmentDownloadService.getForumArticleAttachmentDownloadByFileIdAndUserId(fileId,
                getUserInfoFromSession(session).getUserId());
        result.put("haveDownload", attachmentDownload != null);
        return getSuccessResponseVO(result);
    }

    /**
     * 附件下载
     *
     * @param session
     * @param request
     * @param response
     * @param fileId   附件文件 ID
     */
    @RequestMapping("/attachmentDownload")
    @GlobalInterceptor(checkLogin = true, checkParams = true)
    public void attachmentDownload(HttpSession session, HttpServletRequest request, HttpServletResponse response,
                                   @VerifyParam(required = true) String fileId) {
        // 扣除积分，记录下载记录和消息，并获取附件信息
        ForumArticleAttachment attachment = forumArticleAttachmentService.downloadAttachment(fileId, getUserInfoFromSession(session));
        InputStream in = null;
        OutputStream out = null;
        String downloadFileName = attachment.getFileName();
        // 拼接附件文件存放路径
        String filePath = webConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + Constants.FILE_FOLDER_ATTACHMENT + attachment.getFilePath();
        File file = new File(filePath);
        try {
            in = new FileInputStream(file);
            out = response.getOutputStream();
            response.setContentType("application/x-msdownload; charset=UTF-8");
            // 解决中文文件名乱码问题
            if (request.getHeader("User-Agent").toLowerCase().indexOf("msie") > 0) {//IE浏览器
                downloadFileName = URLEncoder.encode(downloadFileName, "UTF-8");
            } else {
                downloadFileName = new String(downloadFileName.getBytes("UTF-8"), "ISO8859-1");
            }
            response.setHeader("Content-Disposition", "attachment;filename=\"" + downloadFileName + "\"");
            byte[] byteData = new byte[1024];
            int len = 0;
            while ((len = in.read(byteData)) != -1) {
                out.write(byteData, 0, len); // write
            }
            out.flush();
        } catch (Exception e) {
            logger.error("下载异常", e);
            throw new BusinessException("下载失败");
        } finally {
            try {
                if (in != null) {
                    in.close();
                }

            } catch (IOException e) {
                logger.error("IO异常", e);
            }
            try {
                if (out != null) {
                    out.close();
                }

            } catch (IOException e) {
                logger.error("IO异常", e);
            }
        }
    }
}
