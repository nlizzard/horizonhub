package com.nlizzard.horizonhub.mappers;


import com.nlizzard.horizonhub.mappers.basemapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description:用户消息Mapper
 * @author:nlizzard
 * @date:2026/03/08
 */
public interface UserMessageMapper<T, P> extends BaseMapper<T, P> {
    /**
     * 根据MessageId查询
     */
    T selectByMessageId(@Param("messageId") Integer messageId);

    /**
     * 根据MessageId更新
     */
    Integer updateByMessageId(@Param("bean") T t, @Param("messageId") Integer messageId);

    /**
     * 根据MessageId删除
     */
    Integer deleteByMessageId(@Param("messageId") Integer messageId);

    /**
     * 根据ArticleIdAndCommentIdAndSendUserIdAndMessageType查询
     */
    T selectByArticleIdAndCommentIdAndSendUserIdAndMessageType(@Param("articleId") String articleId, @Param("commentId") Integer commentId, @Param("sendUserId") String sendUserId, @Param("messageType") Integer messageType);

    /**
     * 根据ArticleIdAndSendUserIdAndMessageType查询
     */
    T selectByArticleIdAndSendUserIdAndMessageType(@Param("articleId") String articleId, @Param("sendUserId") String sendUserId, @Param("messageType") Integer messageType);

    /**
     * 根据ArticleIdAndCommentIdAndSendUserIdAndMessageType更新
     */
    Integer updateByArticleIdAndCommentIdAndSendUserIdAndMessageType(@Param("bean") T t, @Param("articleId") String articleId, @Param("commentId") Integer commentId, @Param("sendUserId") String sendUserId, @Param("messageType") Integer messageType);

    /**
     * 根据ArticleIdAndCommentIdAndSendUserIdAndMessageType删除
     */
    Integer deleteByArticleIdAndCommentIdAndSendUserIdAndMessageType(@Param("articleId") String articleId, @Param("commentId") Integer commentId, @Param("sendUserId") String sendUserId, @Param("messageType") Integer messageType);


}