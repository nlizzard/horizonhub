package com.nlizzard.horizonhub.controller;

import com.nlizzard.horizonhub.controller.basecontroller.ABaseController;
import com.nlizzard.horizonhub.entity.pojo.UserMessage;
import com.nlizzard.horizonhub.entity.query.UserMessageQuery;
import com.nlizzard.horizonhub.entity.vo.PaginationResultVO;
import com.nlizzard.horizonhub.entity.vo.ResponseVO;
import com.nlizzard.horizonhub.service.UserMessageService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description:用户消息Controller
 * @author:nlizzard
 * @date:2026/03/08
 */
@RestController
@RequestMapping("/userMessage")
public class UserMessageController extends ABaseController {

	@Resource
	private UserMessageService userMessageService;

	/**
	 * 分页查询
	 */
	@RequestMapping("loadDataList")
	public ResponseVO<PaginationResultVO<UserMessage>> loadDataList(UserMessageQuery query) {
		return getSuccessResponseVO(this.userMessageService.findListByPage(query));
	}

	/**
	 * 新增
	 */
	@RequestMapping("add")
	public ResponseVO<Integer> add(UserMessage bean) {
		return getSuccessResponseVO(this.userMessageService.add(bean));
	}

	/**
	 * 批量新增
	 */
	@RequestMapping("addBatch")
	public ResponseVO<Void> addBatch(@RequestBody List<UserMessage> listBean) {
		this.userMessageService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增或修改
	 */
	@RequestMapping("addOrUpdateBatch")
	public ResponseVO<Void> addOrUpdateBatch(@RequestBody List<UserMessage>  listBean) {
		this.userMessageService.addOrUpdateBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据MessageId查询
	 */
	@RequestMapping("getUserMessageByMessageId")
	public ResponseVO<UserMessage> getUserMessageByMessageId(Integer messageId) {
		return getSuccessResponseVO(this.userMessageService.getUserMessageByMessageId(messageId));
	}

	/**
	 * 根据MessageId更新
	 */
	@RequestMapping("updateUserMessageByMessageId")
	public ResponseVO<Void> updateUserMessageByMessageId(UserMessage bean, Integer messageId) {
		this.userMessageService.updateUserMessageByMessageId(bean, messageId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据MessageId删除
	 */
	@RequestMapping("deleteUserMessageByMessageId")
	public ResponseVO<Void> deleteUserMessageByMessageId(Integer messageId) {
		this.userMessageService.deleteUserMessageByMessageId(messageId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据ArticleIdAndCommentIdAndSendUserIdAndMessageType查询
	 */
	@RequestMapping("getUserMessageByArticleIdAndCommentIdAndSendUserIdAndMessageType")
	public ResponseVO<UserMessage> getUserMessageByArticleIdAndCommentIdAndSendUserIdAndMessageType(String articleId, Integer commentId, String sendUserId, Integer messageType) {
		return getSuccessResponseVO(this.userMessageService.getUserMessageByArticleIdAndCommentIdAndSendUserIdAndMessageType(articleId, commentId, sendUserId, messageType));
	}

	/**
	 * 根据ArticleIdAndCommentIdAndSendUserIdAndMessageType更新
	 */
	@RequestMapping("updateUserMessageByArticleIdAndCommentIdAndSendUserIdAndMessageType")
	public ResponseVO<Void> updateUserMessageByArticleIdAndCommentIdAndSendUserIdAndMessageType(UserMessage bean, String articleId, Integer commentId, String sendUserId, Integer messageType) {
		this.userMessageService.updateUserMessageByArticleIdAndCommentIdAndSendUserIdAndMessageType(bean, articleId, commentId, sendUserId, messageType);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据ArticleIdAndCommentIdAndSendUserIdAndMessageType删除
	 */
	@RequestMapping("deleteUserMessageByArticleIdAndCommentIdAndSendUserIdAndMessageType")
	public ResponseVO<Void> deleteUserMessageByArticleIdAndCommentIdAndSendUserIdAndMessageType(String articleId, Integer commentId, String sendUserId, Integer messageType) {
		this.userMessageService.deleteUserMessageByArticleIdAndCommentIdAndSendUserIdAndMessageType(articleId, commentId, sendUserId, messageType);
		return getSuccessResponseVO(null);
	}

}