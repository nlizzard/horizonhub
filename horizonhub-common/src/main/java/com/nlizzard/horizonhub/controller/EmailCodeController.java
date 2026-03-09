package com.nlizzard.horizonhub.controller;

import com.nlizzard.horizonhub.controller.basecontroller.ABaseController;
import com.nlizzard.horizonhub.entity.pojo.EmailCode;
import com.nlizzard.horizonhub.entity.query.EmailCodeQuery;
import com.nlizzard.horizonhub.entity.vo.PaginationResultVO;
import com.nlizzard.horizonhub.entity.vo.ResponseVO;
import com.nlizzard.horizonhub.service.EmailCodeService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * @Description:邮箱验证码Controller
 * @author:nlizzard
 * @date:2026/03/08
 */
@RestController
@RequestMapping("/emailCode")
public class EmailCodeController extends ABaseController {

	@Resource
	private EmailCodeService emailCodeService;

	/**
	 * 分页查询
	 */
	@RequestMapping("loadDataList")
	public ResponseVO<PaginationResultVO<EmailCode>> loadDataList(EmailCodeQuery query) {
		return getSuccessResponseVO(this.emailCodeService.findListByPage(query));
	}

	/**
	 * 新增
	 */
	@RequestMapping("add")
	public ResponseVO<Integer> add(EmailCode bean) {
		return getSuccessResponseVO(this.emailCodeService.add(bean));
	}

	/**
	 * 批量新增
	 */
	@RequestMapping("addBatch")
	public ResponseVO<Void> addBatch(@RequestBody List<EmailCode> listBean) {
		this.emailCodeService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增或修改
	 */
	@RequestMapping("addOrUpdateBatch")
	public ResponseVO<Void> addOrUpdateBatch(@RequestBody List<EmailCode>  listBean) {
		this.emailCodeService.addOrUpdateBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据EmailAndCode查询
	 */
	@RequestMapping("getEmailCodeByEmailAndCode")
	public ResponseVO<EmailCode> getEmailCodeByEmailAndCode(String email, String code) {
		return getSuccessResponseVO(this.emailCodeService.getEmailCodeByEmailAndCode(email, code));
	}

	/**
	 * 根据EmailAndCode更新
	 */
	@RequestMapping("updateEmailCodeByEmailAndCode")
	public ResponseVO<Void> updateEmailCodeByEmailAndCode(EmailCode bean, String email, String code) {
		this.emailCodeService.updateEmailCodeByEmailAndCode(bean, email, code);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据EmailAndCode删除
	 */
	@RequestMapping("deleteEmailCodeByEmailAndCode")
	public ResponseVO<Void> deleteEmailCodeByEmailAndCode(String email, String code) {
		this.emailCodeService.deleteEmailCodeByEmailAndCode(email, code);
		return getSuccessResponseVO(null);
	}

}