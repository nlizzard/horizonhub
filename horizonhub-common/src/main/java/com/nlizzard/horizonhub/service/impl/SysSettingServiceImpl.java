package com.nlizzard.horizonhub.service.impl;

import com.nlizzard.horizonhub.entity.query.basequery.SimplePage;
import com.nlizzard.horizonhub.entity.vo.PaginationResultVO;
import com.nlizzard.horizonhub.entity.pojo.SysSetting;
import com.nlizzard.horizonhub.entity.query.SysSettingQuery;
import com.nlizzard.horizonhub.enums.PageSize;
import com.nlizzard.horizonhub.mappers.SysSettingMapper;
import com.nlizzard.horizonhub.service.SysSettingService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.List;

/**
 * @Description:系统设置信息ServiceImpl
 * @author:nlizzard
 * @date:2026/03/08
 */
@Service("sysSettingService")
public class SysSettingServiceImpl implements SysSettingService {

    @Resource
    private SysSettingMapper<SysSetting, SysSettingQuery> sysSettingMapper;

    /**
     * 根据条件查询列表
     */
    @Override
    public List<SysSetting> findListByParam(SysSettingQuery query) {
        return this.sysSettingMapper.selectList(query);
    }

    /**
     * 根据条件查询数量
     */
    @Override
    public Integer findCountByParam(SysSettingQuery query) {
        return this.sysSettingMapper.selectCount(query);
    }

    /**
     * 分页查询
     */
    @Override
    public PaginationResultVO<SysSetting> findListByPage(SysSettingQuery query) {
        Integer count = this.findCountByParam(query);
        Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
        SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
        query.setSimplePage(page);
        List<SysSetting> list = this.findListByParam(query);
        return new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
    }

    /**
     * 新增
     */
    @Override
    public Integer add(SysSetting bean) {
        return this.sysSettingMapper.insert(bean);
    }

    /**
     * 批量新增
     */
    @Override
    public Integer addBatch(List<SysSetting> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.sysSettingMapper.insertBatch(listBean);
    }

    /**
     * 批量新增或修改
     */
    @Override
    public Integer addOrUpdateBatch(List<SysSetting> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.sysSettingMapper.insertOrUpdateBatch(listBean);
    }

    /**
     * 根据Code查询
     */
    @Override
    public SysSetting getSysSettingByCode(String code) {
        return this.sysSettingMapper.selectByCode(code);
    }

    /**
     * 根据Code更新
     */
    @Override
    public Integer updateSysSettingByCode(SysSetting bean, String code) {
        return this.sysSettingMapper.updateByCode(bean, code);
    }

    /**
     * 根据Code删除
     */
    @Override
    public Integer deleteSysSettingByCode(String code) {
        return this.sysSettingMapper.deleteByCode(code);
    }

}