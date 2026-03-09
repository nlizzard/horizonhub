package com.nlizzard.horizonhub.controller;

import com.nlizzard.horizonhub.controller.basecontroller.ABaseController;
import com.nlizzard.horizonhub.entity.pojo.ForumArticle;
import com.nlizzard.horizonhub.entity.query.ForumArticleQuery;
import com.nlizzard.horizonhub.entity.vo.PaginationResultVO;
import com.nlizzard.horizonhub.entity.vo.ResponseVO;
import com.nlizzard.horizonhub.service.ForumArticleService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * @Description:文章信息Controller
 * @author:nlizzard
 * @date:2026/03/08
 */
@RestController
@RequestMapping("/forumArticle")
public class ForumArticleController extends ABaseController {

	@Resource
	private ForumArticleService forumArticleService;

	/**
	 * 分页查询
	 */
	@RequestMapping("loadDataList")
	public ResponseVO<PaginationResultVO<ForumArticle>> loadDataList(ForumArticleQuery query) {
		return getSuccessResponseVO(this.forumArticleService.findListByPage(query));
	}

	/**
	 * 新增
	 */
	@RequestMapping("add")
	public ResponseVO<Integer> add(ForumArticle bean) {
		return getSuccessResponseVO(this.forumArticleService.add(bean));
	}

	/**
	 * 批量新增
	 */
	@RequestMapping("addBatch")
	public ResponseVO<Void> addBatch(@RequestBody List<ForumArticle> listBean) {
		this.forumArticleService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增或修改
	 */
	@RequestMapping("addOrUpdateBatch")
	public ResponseVO<Void> addOrUpdateBatch(@RequestBody List<ForumArticle>  listBean) {
		this.forumArticleService.addOrUpdateBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据ArticleId查询
	 */
	@RequestMapping("getForumArticleByArticleId")
	public ResponseVO<ForumArticle> getForumArticleByArticleId(String articleId) {
		return getSuccessResponseVO(this.forumArticleService.getForumArticleByArticleId(articleId));
	}

	/**
	 * 根据ArticleId更新
	 */
	@RequestMapping("updateForumArticleByArticleId")
	public ResponseVO<Void> updateForumArticleByArticleId(ForumArticle bean, String articleId) {
		this.forumArticleService.updateForumArticleByArticleId(bean, articleId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据ArticleId删除
	 */
	@RequestMapping("deleteForumArticleByArticleId")
	public ResponseVO<Void> deleteForumArticleByArticleId(String articleId) {
		this.forumArticleService.deleteForumArticleByArticleId(articleId);
		return getSuccessResponseVO(null);
	}

}