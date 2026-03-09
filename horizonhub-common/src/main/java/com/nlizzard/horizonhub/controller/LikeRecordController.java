package com.nlizzard.horizonhub.controller;

import com.nlizzard.horizonhub.controller.basecontroller.ABaseController;
import com.nlizzard.horizonhub.entity.pojo.LikeRecord;
import com.nlizzard.horizonhub.entity.query.LikeRecordQuery;
import com.nlizzard.horizonhub.entity.vo.PaginationResultVO;
import com.nlizzard.horizonhub.entity.vo.ResponseVO;
import com.nlizzard.horizonhub.service.LikeRecordService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * @Description:点赞记录Controller
 * @author:nlizzard
 * @date:2026/03/08
 */
@RestController
@RequestMapping("/likeRecord")
public class LikeRecordController extends ABaseController {

	@Resource
	private LikeRecordService likeRecordService;

	/**
	 * 分页查询
	 */
	@RequestMapping("loadDataList")
	public ResponseVO<PaginationResultVO<LikeRecord>> loadDataList(LikeRecordQuery query) {
		return getSuccessResponseVO(this.likeRecordService.findListByPage(query));
	}

	/**
	 * 新增
	 */
	@RequestMapping("add")
	public ResponseVO<Integer> add(LikeRecord bean) {
		return getSuccessResponseVO(this.likeRecordService.add(bean));
	}

	/**
	 * 批量新增
	 */
	@RequestMapping("addBatch")
	public ResponseVO<Void> addBatch(@RequestBody List<LikeRecord> listBean) {
		this.likeRecordService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增或修改
	 */
	@RequestMapping("addOrUpdateBatch")
	public ResponseVO<Void> addOrUpdateBatch(@RequestBody List<LikeRecord>  listBean) {
		this.likeRecordService.addOrUpdateBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据OpId查询
	 */
	@RequestMapping("getLikeRecordByOpId")
	public ResponseVO<LikeRecord> getLikeRecordByOpId(Integer opId) {
		return getSuccessResponseVO(this.likeRecordService.getLikeRecordByOpId(opId));
	}

	/**
	 * 根据OpId更新
	 */
	@RequestMapping("updateLikeRecordByOpId")
	public ResponseVO<Void> updateLikeRecordByOpId(LikeRecord bean, Integer opId) {
		this.likeRecordService.updateLikeRecordByOpId(bean, opId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据OpId删除
	 */
	@RequestMapping("deleteLikeRecordByOpId")
	public ResponseVO<Void> deleteLikeRecordByOpId(Integer opId) {
		this.likeRecordService.deleteLikeRecordByOpId(opId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据ObjectIdAndUserIdAndOpType查询
	 */
	@RequestMapping("getLikeRecordByObjectIdAndUserIdAndOpType")
	public ResponseVO<LikeRecord> getLikeRecordByObjectIdAndUserIdAndOpType(String objectId, String userId, Integer opType) {
		return getSuccessResponseVO(this.likeRecordService.getLikeRecordByObjectIdAndUserIdAndOpType(objectId, userId, opType));
	}

	/**
	 * 根据ObjectIdAndUserIdAndOpType更新
	 */
	@RequestMapping("updateLikeRecordByObjectIdAndUserIdAndOpType")
	public ResponseVO<Void> updateLikeRecordByObjectIdAndUserIdAndOpType(LikeRecord bean, String objectId, String userId, Integer opType) {
		this.likeRecordService.updateLikeRecordByObjectIdAndUserIdAndOpType(bean, objectId, userId, opType);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据ObjectIdAndUserIdAndOpType删除
	 */
	@RequestMapping("deleteLikeRecordByObjectIdAndUserIdAndOpType")
	public ResponseVO<Void> deleteLikeRecordByObjectIdAndUserIdAndOpType(String objectId, String userId, Integer opType) {
		this.likeRecordService.deleteLikeRecordByObjectIdAndUserIdAndOpType(objectId, userId, opType);
		return getSuccessResponseVO(null);
	}

}