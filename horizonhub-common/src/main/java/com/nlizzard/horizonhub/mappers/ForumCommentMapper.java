package com.nlizzard.horizonhub.mappers;


import com.nlizzard.horizonhub.mappers.basemapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description:评论Mapper
 * @author:nlizzard
 * @date:2026/03/08
 */
public interface ForumCommentMapper<T, P> extends BaseMapper<T, P> {
	/**
	 * 根据CommentId查询
	 */
	T selectByCommentId(@Param("commentId") Integer commentId);

	/**
	 * 根据CommentId更新
	 */
	Integer updateByCommentId(@Param("bean") T t, @Param("commentId") Integer commentId);

	/**
	 * 根据CommentId删除
	 */
	Integer deleteByCommentId(@Param("commentId") Integer commentId);


}