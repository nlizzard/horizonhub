package com.nlizzard.horizonhub.controller;

import com.nlizzard.horizonhub.controller.basecontroller.ABaseController;
import com.nlizzard.horizonhub.entity.pojo.ForumArticleAttachment;
import com.nlizzard.horizonhub.entity.query.ForumArticleAttachmentQuery;
import com.nlizzard.horizonhub.entity.vo.PaginationResultVO;
import com.nlizzard.horizonhub.entity.vo.ResponseVO;
import com.nlizzard.horizonhub.service.ForumArticleAttachmentService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * @Description:文件信息Controller
 * @author:nlizzard
 * @date:2026/03/08
 */
@RestController
@RequestMapping("/forumArticleAttachment")
public class ForumArticleAttachmentController extends ABaseController {

	@Resource
	private ForumArticleAttachmentService forumArticleAttachmentService;

	/**
	 * 分页查询
	 */
	@RequestMapping("loadDataList")
	public ResponseVO<PaginationResultVO<ForumArticleAttachment>> loadDataList(ForumArticleAttachmentQuery query) {
		return getSuccessResponseVO(this.forumArticleAttachmentService.findListByPage(query));
	}

	/**
	 * 新增
	 */
	@RequestMapping("add")
	public ResponseVO<Integer> add(ForumArticleAttachment bean) {
		return getSuccessResponseVO(this.forumArticleAttachmentService.add(bean));
	}

	/**
	 * 批量新增
	 */
	@RequestMapping("addBatch")
	public ResponseVO<Void> addBatch(@RequestBody List<ForumArticleAttachment> listBean) {
		this.forumArticleAttachmentService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增或修改
	 */
	@RequestMapping("addOrUpdateBatch")
	public ResponseVO<Void> addOrUpdateBatch(@RequestBody List<ForumArticleAttachment>  listBean) {
		this.forumArticleAttachmentService.addOrUpdateBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据FileId查询
	 */
	@RequestMapping("getForumArticleAttachmentByFileId")
	public ResponseVO<ForumArticleAttachment> getForumArticleAttachmentByFileId(String fileId) {
		return getSuccessResponseVO(this.forumArticleAttachmentService.getForumArticleAttachmentByFileId(fileId));
	}

	/**
	 * 根据FileId更新
	 */
	@RequestMapping("updateForumArticleAttachmentByFileId")
	public ResponseVO<Void> updateForumArticleAttachmentByFileId(ForumArticleAttachment bean, String fileId) {
		this.forumArticleAttachmentService.updateForumArticleAttachmentByFileId(bean, fileId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据FileId删除
	 */
	@RequestMapping("deleteForumArticleAttachmentByFileId")
	public ResponseVO<Void> deleteForumArticleAttachmentByFileId(String fileId) {
		this.forumArticleAttachmentService.deleteForumArticleAttachmentByFileId(fileId);
		return getSuccessResponseVO(null);
	}

}