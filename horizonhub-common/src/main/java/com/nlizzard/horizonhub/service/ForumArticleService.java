package com.nlizzard.horizonhub.service;

import com.nlizzard.horizonhub.entity.pojo.ForumArticle;
import com.nlizzard.horizonhub.entity.query.ForumArticleQuery;
import com.nlizzard.horizonhub.entity.vo.PaginationResultVO;

import java.util.List;

/**
 * @Description:文章信息Service
 * @author:nlizzard
 * @date:2026/03/08
 */
public interface ForumArticleService {

    /**
     * 根据条件查询列表
     */
    List<ForumArticle> findListByParam(ForumArticleQuery query);

    /**
     * 根据条件查询数量
     */
    Integer findCountByParam(ForumArticleQuery query);

    /**
     * 分页查询
     */
    PaginationResultVO<ForumArticle> findListByPage(ForumArticleQuery query);

    /**
     * 新增
     */
    Integer add(ForumArticle bean);

    /**
     * 批量新增
     */
    Integer addBatch(List<ForumArticle> listBean);

    /**
     * 批量新增或修改
     */
    Integer addOrUpdateBatch(List<ForumArticle> listBean);

    /**
     * 根据ArticleId查询
     */
    ForumArticle getForumArticleByArticleId(String articleId);

    /**
     * 根据ArticleId更新
     */
    void updateForumArticleByArticleId(ForumArticle bean, String articleId);

    /**
     * 根据ArticleId删除
     */
    Integer deleteForumArticleByArticleId(String articleId);

    /**
     * 文章详情获取
     *
     * @param articleId 文章 ID
     */
    ForumArticle readArticle(String articleId);
}