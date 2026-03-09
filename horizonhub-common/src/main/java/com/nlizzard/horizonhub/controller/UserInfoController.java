package com.nlizzard.horizonhub.controller;

import com.nlizzard.horizonhub.controller.basecontroller.ABaseController;
import com.nlizzard.horizonhub.entity.pojo.UserInfo;
import com.nlizzard.horizonhub.entity.query.UserInfoQuery;
import com.nlizzard.horizonhub.entity.vo.PaginationResultVO;
import com.nlizzard.horizonhub.entity.vo.ResponseVO;
import com.nlizzard.horizonhub.service.UserInfoService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * @Description:用户信息Controller
 * @author:nlizzard
 * @date:2026/03/08
 */
@RestController
@RequestMapping("/userInfo")
public class UserInfoController extends ABaseController {

	@Resource
	private UserInfoService userInfoService;

	/**
	 * 分页查询
	 */
	@RequestMapping("loadDataList")
	public ResponseVO<PaginationResultVO<UserInfo>> loadDataList(UserInfoQuery query) {
		return getSuccessResponseVO(this.userInfoService.findListByPage(query));
	}

	/**
	 * 新增
	 */
	@RequestMapping("add")
	public ResponseVO<Integer> add(UserInfo bean) {
		return getSuccessResponseVO(this.userInfoService.add(bean));
	}

	/**
	 * 批量新增
	 */
	@RequestMapping("addBatch")
	public ResponseVO<Void> addBatch(@RequestBody List<UserInfo> listBean) {
		this.userInfoService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增或修改
	 */
	@RequestMapping("addOrUpdateBatch")
	public ResponseVO<Void> addOrUpdateBatch(@RequestBody List<UserInfo>  listBean) {
		this.userInfoService.addOrUpdateBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据UserId查询
	 */
	@RequestMapping("getUserInfoByUserId")
	public ResponseVO<UserInfo> getUserInfoByUserId(String userId) {
		return getSuccessResponseVO(this.userInfoService.getUserInfoByUserId(userId));
	}

	/**
	 * 根据UserId更新
	 */
	@RequestMapping("updateUserInfoByUserId")
	public ResponseVO<Void> updateUserInfoByUserId(UserInfo bean, String userId) {
		this.userInfoService.updateUserInfoByUserId(bean, userId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据UserId删除
	 */
	@RequestMapping("deleteUserInfoByUserId")
	public ResponseVO<Void> deleteUserInfoByUserId(String userId) {
		this.userInfoService.deleteUserInfoByUserId(userId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据Email查询
	 */
	@RequestMapping("getUserInfoByEmail")
	public ResponseVO<UserInfo> getUserInfoByEmail(String email) {
		return getSuccessResponseVO(this.userInfoService.getUserInfoByEmail(email));
	}

	/**
	 * 根据Email更新
	 */
	@RequestMapping("updateUserInfoByEmail")
	public ResponseVO<Void> updateUserInfoByEmail(UserInfo bean, String email) {
		this.userInfoService.updateUserInfoByEmail(bean, email);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据Email删除
	 */
	@RequestMapping("deleteUserInfoByEmail")
	public ResponseVO<Void> deleteUserInfoByEmail(String email) {
		this.userInfoService.deleteUserInfoByEmail(email);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据NickName查询
	 */
	@RequestMapping("getUserInfoByNickName")
	public ResponseVO<UserInfo> getUserInfoByNickName(String nickName) {
		return getSuccessResponseVO(this.userInfoService.getUserInfoByNickName(nickName));
	}

	/**
	 * 根据NickName更新
	 */
	@RequestMapping("updateUserInfoByNickName")
	public ResponseVO<Void> updateUserInfoByNickName(UserInfo bean, String nickName) {
		this.userInfoService.updateUserInfoByNickName(bean, nickName);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据NickName删除
	 */
	@RequestMapping("deleteUserInfoByNickName")
	public ResponseVO<Void> deleteUserInfoByNickName(String nickName) {
		this.userInfoService.deleteUserInfoByNickName(nickName);
		return getSuccessResponseVO(null);
	}

}