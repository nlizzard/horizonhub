package com.nlizzard.horizonhub.service;

import com.nlizzard.horizonhub.entity.pojo.ForumArticleAttachment;
import com.nlizzard.horizonhub.entity.query.ForumArticleAttachmentQuery;
import com.nlizzard.horizonhub.entity.vo.PaginationResultVO;

import java.util.List;

/**
 * @Description:文件信息Service
 * @author:nlizzard
 * @date:2026/03/08
 */
public interface ForumArticleAttachmentService {

    /**
     * 根据条件查询列表
     */
    List<ForumArticleAttachment> findListByParam(ForumArticleAttachmentQuery query);

    /**
     * 根据条件查询数量
     */
    Integer findCountByParam(ForumArticleAttachmentQuery query);

    /**
     * 分页查询
     */
    PaginationResultVO<ForumArticleAttachment> findListByPage(ForumArticleAttachmentQuery query);

    /**
     * 新增
     */
    Integer add(ForumArticleAttachment bean);

    /**
     * 批量新增
     */
    Integer addBatch(List<ForumArticleAttachment> listBean);

    /**
     * 批量新增或修改
     */
    Integer addOrUpdateBatch(List<ForumArticleAttachment> listBean);

    /**
     * 根据FileId查询
     */
    ForumArticleAttachment getForumArticleAttachmentByFileId(String fileId);

    /**
     * 根据FileId更新
     */
    void updateForumArticleAttachmentByFileId(ForumArticleAttachment bean, String fileId);

    /**
     * 根据FileId删除
     */
    void deleteForumArticleAttachmentByFileId(String fileId);

}