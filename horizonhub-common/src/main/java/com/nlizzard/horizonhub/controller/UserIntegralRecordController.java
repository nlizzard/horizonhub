package com.nlizzard.horizonhub.controller;

import com.nlizzard.horizonhub.controller.basecontroller.ABaseController;
import com.nlizzard.horizonhub.entity.pojo.UserIntegralRecord;
import com.nlizzard.horizonhub.entity.query.UserIntegralRecordQuery;
import com.nlizzard.horizonhub.entity.vo.PaginationResultVO;
import com.nlizzard.horizonhub.entity.vo.ResponseVO;
import com.nlizzard.horizonhub.service.UserIntegralRecordService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * @Description:用户积分记录表Controller
 * @author:nlizzard
 * @date:2026/03/08
 */
@RestController
@RequestMapping("/userIntegralRecord")
public class UserIntegralRecordController extends ABaseController {

	@Resource
	private UserIntegralRecordService userIntegralRecordService;

	/**
	 * 分页查询
	 */
	@RequestMapping("loadDataList")
	public ResponseVO<PaginationResultVO<UserIntegralRecord>> loadDataList(UserIntegralRecordQuery query) {
		return getSuccessResponseVO(this.userIntegralRecordService.findListByPage(query));
	}

	/**
	 * 新增
	 */
	@RequestMapping("add")
	public ResponseVO<Integer> add(UserIntegralRecord bean) {
		return getSuccessResponseVO(this.userIntegralRecordService.add(bean));
	}

	/**
	 * 批量新增
	 */
	@RequestMapping("addBatch")
	public ResponseVO<Void> addBatch(@RequestBody List<UserIntegralRecord> listBean) {
		this.userIntegralRecordService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增或修改
	 */
	@RequestMapping("addOrUpdateBatch")
	public ResponseVO<Void> addOrUpdateBatch(@RequestBody List<UserIntegralRecord>  listBean) {
		this.userIntegralRecordService.addOrUpdateBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据RecordId查询
	 */
	@RequestMapping("getUserIntegralRecordByRecordId")
	public ResponseVO<UserIntegralRecord> getUserIntegralRecordByRecordId(Integer recordId) {
		return getSuccessResponseVO(this.userIntegralRecordService.getUserIntegralRecordByRecordId(recordId));
	}

	/**
	 * 根据RecordId更新
	 */
	@RequestMapping("updateUserIntegralRecordByRecordId")
	public ResponseVO<Void> updateUserIntegralRecordByRecordId(UserIntegralRecord bean, Integer recordId) {
		this.userIntegralRecordService.updateUserIntegralRecordByRecordId(bean, recordId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据RecordId删除
	 */
	@RequestMapping("deleteUserIntegralRecordByRecordId")
	public ResponseVO<Void> deleteUserIntegralRecordByRecordId(Integer recordId) {
		this.userIntegralRecordService.deleteUserIntegralRecordByRecordId(recordId);
		return getSuccessResponseVO(null);
	}

}