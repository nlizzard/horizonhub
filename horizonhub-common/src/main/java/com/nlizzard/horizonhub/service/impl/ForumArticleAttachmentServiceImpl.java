package com.nlizzard.horizonhub.service.impl;

import com.nlizzard.horizonhub.entity.pojo.ForumArticleAttachment;
import com.nlizzard.horizonhub.entity.query.ForumArticleAttachmentQuery;
import com.nlizzard.horizonhub.entity.query.basequery.SimplePage;
import com.nlizzard.horizonhub.entity.vo.PaginationResultVO;
import com.nlizzard.horizonhub.enums.PageSize;
import com.nlizzard.horizonhub.mappers.ForumArticleAttachmentMapper;
import com.nlizzard.horizonhub.service.ForumArticleAttachmentService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:文件信息ServiceImpl
 * @author:nlizzard
 * @date:2026/03/08
 */
@Service("forumArticleAttachmentService")
public class ForumArticleAttachmentServiceImpl implements ForumArticleAttachmentService {

    @Resource
    private ForumArticleAttachmentMapper<ForumArticleAttachment, ForumArticleAttachmentQuery> forumArticleAttachmentMapper;

    /**
     * 根据条件查询列表
     */
    @Override
    public List<ForumArticleAttachment> findListByParam(ForumArticleAttachmentQuery query) {
        return this.forumArticleAttachmentMapper.selectList(query);
    }

    /**
     * 根据条件查询数量
     */
    @Override
    public Integer findCountByParam(ForumArticleAttachmentQuery query) {
        return this.forumArticleAttachmentMapper.selectCount(query);
    }

    /**
     * 分页查询
     */
    @Override
    public PaginationResultVO<ForumArticleAttachment> findListByPage(ForumArticleAttachmentQuery query) {
        Integer count = this.findCountByParam(query);
        Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
        SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
        query.setSimplePage(page);
        List<ForumArticleAttachment> list = this.findListByParam(query);
        return new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
    }

    /**
     * 新增
     */
    @Override
    public Integer add(ForumArticleAttachment bean) {
        return this.forumArticleAttachmentMapper.insert(bean);
    }

    /**
     * 批量新增
     */
    @Override
    public Integer addBatch(List<ForumArticleAttachment> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.forumArticleAttachmentMapper.insertBatch(listBean);
    }

    /**
     * 批量新增或修改
     */
    @Override
    public Integer addOrUpdateBatch(List<ForumArticleAttachment> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.forumArticleAttachmentMapper.insertOrUpdateBatch(listBean);
    }

    /**
     * 根据FileId查询
     */
    @Override
    public ForumArticleAttachment getForumArticleAttachmentByFileId(String fileId) {
        return this.forumArticleAttachmentMapper.selectByFileId(fileId);
    }

    /**
     * 根据FileId更新
     */
    @Override
    public void updateForumArticleAttachmentByFileId(ForumArticleAttachment bean, String fileId) {
        this.forumArticleAttachmentMapper.updateByFileId(bean, fileId);
    }

    /**
     * 根据FileId删除
     */
    @Override
    public void deleteForumArticleAttachmentByFileId(String fileId) {
        this.forumArticleAttachmentMapper.deleteByFileId(fileId);
    }

}