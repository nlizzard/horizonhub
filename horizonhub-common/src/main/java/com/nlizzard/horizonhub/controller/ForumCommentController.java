package com.nlizzard.horizonhub.controller;

import com.nlizzard.horizonhub.controller.basecontroller.ABaseController;
import com.nlizzard.horizonhub.entity.pojo.ForumComment;
import com.nlizzard.horizonhub.entity.query.ForumCommentQuery;
import com.nlizzard.horizonhub.entity.vo.PaginationResultVO;
import com.nlizzard.horizonhub.entity.vo.ResponseVO;
import com.nlizzard.horizonhub.service.ForumCommentService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * @Description:评论Controller
 * @author:nlizzard
 * @date:2026/03/08
 */
@RestController
@RequestMapping("/forumComment")
public class ForumCommentController extends ABaseController {

	@Resource
	private ForumCommentService forumCommentService;

	/**
	 * 分页查询
	 */
	@RequestMapping("loadDataList")
	public ResponseVO<PaginationResultVO<ForumComment>> loadDataList(ForumCommentQuery query) {
		return getSuccessResponseVO(this.forumCommentService.findListByPage(query));
	}

	/**
	 * 新增
	 */
	@RequestMapping("add")
	public ResponseVO<Integer> add(ForumComment bean) {
		return getSuccessResponseVO(this.forumCommentService.add(bean));
	}

	/**
	 * 批量新增
	 */
	@RequestMapping("addBatch")
	public ResponseVO<Void> addBatch(@RequestBody List<ForumComment> listBean) {
		this.forumCommentService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增或修改
	 */
	@RequestMapping("addOrUpdateBatch")
	public ResponseVO<Void> addOrUpdateBatch(@RequestBody List<ForumComment>  listBean) {
		this.forumCommentService.addOrUpdateBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据CommentId查询
	 */
	@RequestMapping("getForumCommentByCommentId")
	public ResponseVO<ForumComment> getForumCommentByCommentId(Integer commentId) {
		return getSuccessResponseVO(this.forumCommentService.getForumCommentByCommentId(commentId));
	}

	/**
	 * 根据CommentId更新
	 */
	@RequestMapping("updateForumCommentByCommentId")
	public ResponseVO<Void> updateForumCommentByCommentId(ForumComment bean, Integer commentId) {
		this.forumCommentService.updateForumCommentByCommentId(bean, commentId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据CommentId删除
	 */
	@RequestMapping("deleteForumCommentByCommentId")
	public ResponseVO<Void> deleteForumCommentByCommentId(Integer commentId) {
		this.forumCommentService.deleteForumCommentByCommentId(commentId);
		return getSuccessResponseVO(null);
	}

}