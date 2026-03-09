package com.nlizzard.horizonhub.controller;

import com.nlizzard.horizonhub.controller.basecontroller.ABaseController;
import com.nlizzard.horizonhub.entity.pojo.ForumArticleAttachmentDownload;
import com.nlizzard.horizonhub.entity.query.ForumArticleAttachmentDownloadQuery;
import com.nlizzard.horizonhub.entity.vo.PaginationResultVO;
import com.nlizzard.horizonhub.entity.vo.ResponseVO;
import com.nlizzard.horizonhub.service.ForumArticleAttachmentDownloadService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * @Description:用户附件下载Controller
 * @author:nlizzard
 * @date:2026/03/08
 */
@RestController
@RequestMapping("/forumArticleAttachmentDownload")
public class ForumArticleAttachmentDownloadController extends ABaseController {

	@Resource
	private ForumArticleAttachmentDownloadService forumArticleAttachmentDownloadService;

	/**
	 * 分页查询
	 */
	@RequestMapping("loadDataList")
	public ResponseVO<PaginationResultVO<ForumArticleAttachmentDownload>> loadDataList(ForumArticleAttachmentDownloadQuery query) {
		return getSuccessResponseVO(this.forumArticleAttachmentDownloadService.findListByPage(query));
	}

	/**
	 * 新增
	 */
	@RequestMapping("add")
	public ResponseVO<Integer> add(ForumArticleAttachmentDownload bean) {
		return getSuccessResponseVO(this.forumArticleAttachmentDownloadService.add(bean));
	}

	/**
	 * 批量新增
	 */
	@RequestMapping("addBatch")
	public ResponseVO<Void> addBatch(@RequestBody List<ForumArticleAttachmentDownload> listBean) {
		this.forumArticleAttachmentDownloadService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增或修改
	 */
	@RequestMapping("addOrUpdateBatch")
	public ResponseVO<Void> addOrUpdateBatch(@RequestBody List<ForumArticleAttachmentDownload>  listBean) {
		this.forumArticleAttachmentDownloadService.addOrUpdateBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据FileIdAndUserId查询
	 */
	@RequestMapping("getForumArticleAttachmentDownloadByFileIdAndUserId")
	public ResponseVO<ForumArticleAttachmentDownload> getForumArticleAttachmentDownloadByFileIdAndUserId(String fileId, String userId) {
		return getSuccessResponseVO(this.forumArticleAttachmentDownloadService.getForumArticleAttachmentDownloadByFileIdAndUserId(fileId, userId));
	}

	/**
	 * 根据FileIdAndUserId更新
	 */
	@RequestMapping("updateForumArticleAttachmentDownloadByFileIdAndUserId")
	public ResponseVO<Void> updateForumArticleAttachmentDownloadByFileIdAndUserId(ForumArticleAttachmentDownload bean, String fileId, String userId) {
		this.forumArticleAttachmentDownloadService.updateForumArticleAttachmentDownloadByFileIdAndUserId(bean, fileId, userId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据FileIdAndUserId删除
	 */
	@RequestMapping("deleteForumArticleAttachmentDownloadByFileIdAndUserId")
	public ResponseVO<Void> deleteForumArticleAttachmentDownloadByFileIdAndUserId(String fileId, String userId) {
		this.forumArticleAttachmentDownloadService.deleteForumArticleAttachmentDownloadByFileIdAndUserId(fileId, userId);
		return getSuccessResponseVO(null);
	}

}