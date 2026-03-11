package com.nlizzard.horizonhub.entity.vo.web;

import java.io.Serializable;

public class FormArticleDetailVO implements Serializable {
    private ForumArticleVO forumArticle;
    private ForumArticleAttachmentVo attachment;
    private Boolean haveLike = false;

    public Boolean getHaveLike() {
        return haveLike;
    }

    public void setHaveLike(Boolean haveLike) {
        this.haveLike = haveLike;
    }

    public ForumArticleVO getForumArticle() {
        return forumArticle;
    }

    public void setForumArticle(ForumArticleVO forumArticle) {
        this.forumArticle = forumArticle;
    }

    public ForumArticleAttachmentVo getAttachment() {
        return attachment;
    }

    public void setAttachment(ForumArticleAttachmentVo attachment) {
        this.attachment = attachment;
    }

}
