package com.nlizzard.horizonhub.controller;

import com.nlizzard.horizonhub.controller.basecontroller.ABaseController;
import com.nlizzard.horizonhub.entity.pojo.SysSetting;
import com.nlizzard.horizonhub.entity.query.SysSettingQuery;
import com.nlizzard.horizonhub.entity.vo.PaginationResultVO;
import com.nlizzard.horizonhub.entity.vo.ResponseVO;
import com.nlizzard.horizonhub.service.SysSettingService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * @Description:系统设置信息Controller
 * @author:nlizzard
 * @date:2026/03/08
 */
@RestController
@RequestMapping("/sysSetting")
public class SysSettingController extends ABaseController {

	@Resource
	private SysSettingService sysSettingService;

	/**
	 * 分页查询
	 */
	@RequestMapping("loadDataList")
	public ResponseVO<PaginationResultVO<SysSetting>> loadDataList(SysSettingQuery query) {
		return getSuccessResponseVO(this.sysSettingService.findListByPage(query));
	}

	/**
	 * 新增
	 */
	@RequestMapping("add")
	public ResponseVO<Integer> add(SysSetting bean) {
		return getSuccessResponseVO(this.sysSettingService.add(bean));
	}

	/**
	 * 批量新增
	 */
	@RequestMapping("addBatch")
	public ResponseVO<Void> addBatch(@RequestBody List<SysSetting> listBean) {
		this.sysSettingService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增或修改
	 */
	@RequestMapping("addOrUpdateBatch")
	public ResponseVO<Void> addOrUpdateBatch(@RequestBody List<SysSetting>  listBean) {
		this.sysSettingService.addOrUpdateBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据Code查询
	 */
	@RequestMapping("getSysSettingByCode")
	public ResponseVO<SysSetting> getSysSettingByCode(String code) {
		return getSuccessResponseVO(this.sysSettingService.getSysSettingByCode(code));
	}

	/**
	 * 根据Code更新
	 */
	@RequestMapping("updateSysSettingByCode")
	public ResponseVO<Void> updateSysSettingByCode(SysSetting bean, String code) {
		this.sysSettingService.updateSysSettingByCode(bean, code);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据Code删除
	 */
	@RequestMapping("deleteSysSettingByCode")
	public ResponseVO<Void> deleteSysSettingByCode(String code) {
		this.sysSettingService.deleteSysSettingByCode(code);
		return getSuccessResponseVO(null);
	}

}