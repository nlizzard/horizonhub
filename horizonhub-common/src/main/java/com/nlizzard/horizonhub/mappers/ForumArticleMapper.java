package com.nlizzard.horizonhub.mappers;


import com.nlizzard.horizonhub.mappers.basemapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description:文章信息Mapper
 * @author:nlizzard
 * @date:2026/03/08
 */
public interface ForumArticleMapper<T, P> extends BaseMapper<T, P> {
	/**
	 * 根据ArticleId查询
	 */
	T selectByArticleId(@Param("articleId") String articleId);

	/**
	 * 根据ArticleId更新
	 */
	Integer updateByArticleId(@Param("bean") T t, @Param("articleId") String articleId);

	/**
	 * 根据ArticleId删除
	 */
	Integer deleteByArticleId(@Param("articleId") String articleId);


}