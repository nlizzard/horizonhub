package com.nlizzard.horizonhub.service.impl;

import com.nlizzard.horizonhub.entity.query.basequery.SimplePage;
import com.nlizzard.horizonhub.entity.vo.PaginationResultVO;
import com.nlizzard.horizonhub.entity.pojo.ForumBoard;
import com.nlizzard.horizonhub.entity.query.ForumBoardQuery;
import com.nlizzard.horizonhub.enums.PageSize;
import com.nlizzard.horizonhub.mappers.ForumBoardMapper;
import com.nlizzard.horizonhub.service.ForumBoardService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.List;

/**
 * @Description:文章板块信息ServiceImpl
 * @author:nlizzard
 * @date:2026/03/08
 */
@Service("forumBoardService")
public class ForumBoardServiceImpl implements ForumBoardService {

    @Resource
    private ForumBoardMapper<ForumBoard, ForumBoardQuery> forumBoardMapper;

    /**
     * 根据条件查询列表
     */
    @Override
    public List<ForumBoard> findListByParam(ForumBoardQuery query) {
        return this.forumBoardMapper.selectList(query);
    }

    /**
     * 根据条件查询数量
     */
    @Override
    public Integer findCountByParam(ForumBoardQuery query) {
        return this.forumBoardMapper.selectCount(query);
    }

    /**
     * 分页查询
     */
    @Override
    public PaginationResultVO<ForumBoard> findListByPage(ForumBoardQuery query) {
        Integer count = this.findCountByParam(query);
        Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
        SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
        query.setSimplePage(page);
        List<ForumBoard> list = this.findListByParam(query);
        return new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
    }

    /**
     * 新增
     */
    @Override
    public Integer add(ForumBoard bean) {
        return this.forumBoardMapper.insert(bean);
    }

    /**
     * 批量新增
     */
    @Override
    public Integer addBatch(List<ForumBoard> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.forumBoardMapper.insertBatch(listBean);
    }

    /**
     * 批量新增或修改
     */
    @Override
    public Integer addOrUpdateBatch(List<ForumBoard> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.forumBoardMapper.insertOrUpdateBatch(listBean);
    }

    /**
     * 根据BoardId查询
     */
    @Override
    public ForumBoard getForumBoardByBoardId(Integer boardId) {
        return this.forumBoardMapper.selectByBoardId(boardId);
    }

    /**
     * 根据BoardId更新
     */
    @Override
    public Integer updateForumBoardByBoardId(ForumBoard bean, Integer boardId) {
        return this.forumBoardMapper.updateByBoardId(bean, boardId);
    }

    /**
     * 根据BoardId删除
     */
    @Override
    public Integer deleteForumBoardByBoardId(Integer boardId) {
        return this.forumBoardMapper.deleteByBoardId(boardId);
    }

}