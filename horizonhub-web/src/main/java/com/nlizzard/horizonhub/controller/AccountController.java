package com.nlizzard.horizonhub.controller;


import com.nlizzard.horizonhub.basecontroller.BaseController;
import com.nlizzard.horizonhub.constants.Constants;
import com.nlizzard.horizonhub.entity.dto.CreateImageCode;
import com.nlizzard.horizonhub.entity.vo.ResponseVO;
import com.nlizzard.horizonhub.exception.BusinessException;
import com.nlizzard.horizonhub.service.EmailCodeService;
import com.nlizzard.horizonhub.service.UserInfoService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * 账号相关controller
 */
@RestController
public class AccountController extends BaseController {

    @Resource
    private EmailCodeService emailCodeService;

    @Resource
    private UserInfoService userInfoService;

    /**
     * 生成图片验证码
     *
     * @param response
     * @param session
     * @param type     0：登录注册图片验证码，1：发送邮件图片验证码
     * @throws IOException
     */
    @GetMapping(value = "/checkCode")
    public void checkCode(HttpServletResponse response, HttpSession session, Integer type) throws
            IOException {
        // 创建图片验证码对象
        CreateImageCode codeEntity = new CreateImageCode(130, 38, 5, 10);
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        // 将验证码存入session
        String code = codeEntity.getCode();
        if (type == null || type == 0) {
            // 注册邮箱验证码
            session.setAttribute(Constants.CHECK_CODE_KEY, code);
        } else {
            // 邮箱验证码
            session.setAttribute(Constants.CHECK_CODE_KEY_EMAIL, code);
        }
        codeEntity.write(response.getOutputStream());
    }

    /**
     * 发送邮箱验证码
     *
     * @param session
     * @param email
     * @param checkCode
     * @param type      0-注册验证码，1-找回密码验证码
     * @return
     */
    @GetMapping("/sendEmailCode")
    public ResponseVO<Void> sendEmailCode(HttpSession session,
                                          String email,
                                          String checkCode,
                                          Integer type) {
        try {
            if (!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY_EMAIL))) {
                throw new BusinessException("图片验证码不正确");
            }
            emailCodeService.sendEmailCode(email, type);
            return getSuccessResponseVO(null);
        } finally {
            session.removeAttribute(Constants.CHECK_CODE_KEY_EMAIL);
        }
    }

    /**
     * 注册账号接口
     *
     * @param session
     * @param email
     * @param nickName
     * @param password
     * @param checkCode
     * @param emailCode
     * @return
     */
    @PostMapping("/register")
    public ResponseVO<Void> register(HttpSession session,
                                     String email,
                                     String nickName,
                                     String password,
                                     String checkCode,
                                     String emailCode) {
        try {
            if (!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY))) {
                throw new BusinessException("图片验证码不正确");
            }
            userInfoService.register(email, nickName, password, emailCode);
            return getSuccessResponseVO(null);
        } finally {
            session.removeAttribute(Constants.CHECK_CODE_KEY);
        }
    }
}
