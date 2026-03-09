package com.nlizzard.horizonhub.controller;

import com.nlizzard.horizonhub.controller.basecontroller.ABaseController;
import com.nlizzard.horizonhub.entity.pojo.ForumBoard;
import com.nlizzard.horizonhub.entity.query.ForumBoardQuery;
import com.nlizzard.horizonhub.entity.vo.PaginationResultVO;
import com.nlizzard.horizonhub.entity.vo.ResponseVO;
import com.nlizzard.horizonhub.service.ForumBoardService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * @Description:文章板块信息Controller
 * @author:nlizzard
 * @date:2026/03/08
 */
@RestController
@RequestMapping("/forumBoard")
public class ForumBoardController extends ABaseController {

	@Resource
	private ForumBoardService forumBoardService;

	/**
	 * 分页查询
	 */
	@RequestMapping("loadDataList")
	public ResponseVO<PaginationResultVO<ForumBoard>> loadDataList(ForumBoardQuery query) {
		return getSuccessResponseVO(this.forumBoardService.findListByPage(query));
	}

	/**
	 * 新增
	 */
	@RequestMapping("add")
	public ResponseVO<Integer> add(ForumBoard bean) {
		return getSuccessResponseVO(this.forumBoardService.add(bean));
	}

	/**
	 * 批量新增
	 */
	@RequestMapping("addBatch")
	public ResponseVO<Void> addBatch(@RequestBody List<ForumBoard> listBean) {
		this.forumBoardService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增或修改
	 */
	@RequestMapping("addOrUpdateBatch")
	public ResponseVO<Void> addOrUpdateBatch(@RequestBody List<ForumBoard>  listBean) {
		this.forumBoardService.addOrUpdateBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据BoardId查询
	 */
	@RequestMapping("getForumBoardByBoardId")
	public ResponseVO<ForumBoard> getForumBoardByBoardId(Integer boardId) {
		return getSuccessResponseVO(this.forumBoardService.getForumBoardByBoardId(boardId));
	}

	/**
	 * 根据BoardId更新
	 */
	@RequestMapping("updateForumBoardByBoardId")
	public ResponseVO<Void> updateForumBoardByBoardId(ForumBoard bean, Integer boardId) {
		this.forumBoardService.updateForumBoardByBoardId(bean, boardId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据BoardId删除
	 */
	@RequestMapping("deleteForumBoardByBoardId")
	public ResponseVO<Void> deleteForumBoardByBoardId(Integer boardId) {
		this.forumBoardService.deleteForumBoardByBoardId(boardId);
		return getSuccessResponseVO(null);
	}

}