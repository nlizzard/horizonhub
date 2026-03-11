package com.nlizzard.horizonhub.service.impl;

import com.nlizzard.horizonhub.entity.enums.PageSize;
import com.nlizzard.horizonhub.entity.pojo.ForumBoard;
import com.nlizzard.horizonhub.entity.query.ForumBoardQuery;
import com.nlizzard.horizonhub.entity.query.basequery.SimplePage;
import com.nlizzard.horizonhub.entity.vo.PaginationResultVO;
import com.nlizzard.horizonhub.mappers.ForumBoardMapper;
import com.nlizzard.horizonhub.service.ForumBoardService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    /**
     * 获取板块树
     *
     * @param postType 文章类型，1表示查询所有
     */
    @Override
    public List<ForumBoard> getBoardTree(Integer postType) {
        ForumBoardQuery forumBoardQuery = new ForumBoardQuery();
        forumBoardQuery.setOrderBy("sort ASC");
        forumBoardQuery.setPostType(postType);
        List<ForumBoard> forumBoardList = forumBoardMapper.selectList(forumBoardQuery);
        return convertLine2Tree(forumBoardList, 0);
    }

    // 递归算法，将线性结构转换为树形结构  TODO:后续可以优化为非递归算法，减少递归调用的性能开销
    private List<ForumBoard> convertLine2Tree(List<ForumBoard> forumBoardList, Integer pBoardId) {
        List<ForumBoard> treeList = new ArrayList<>();
        for (ForumBoard forumBoard : forumBoardList) {
            if (forumBoard.getPBoardId().equals(pBoardId)) {
                forumBoard.setChildren(convertLine2Tree(forumBoardList, forumBoard.getBoardId()));
                treeList.add(forumBoard);
            }
        }
        return treeList;
    }
}