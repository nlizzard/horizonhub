package com.nlizzard.horizonhub.controller;

import cn.hutool.crypto.SecureUtil;
import com.nlizzard.horizonhub.annotation.GlobalInterceptor;
import com.nlizzard.horizonhub.annotation.VerifyParam;
import com.nlizzard.horizonhub.basecontroller.BaseController;
import com.nlizzard.horizonhub.constants.Constants;
import com.nlizzard.horizonhub.entity.config.AdminConfig;
import com.nlizzard.horizonhub.entity.dto.CreateImageCode;
import com.nlizzard.horizonhub.entity.dto.SessionAdminUserDto;
import com.nlizzard.horizonhub.entity.vo.ResponseVO;
import com.nlizzard.horizonhub.exception.BusinessException;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class AccountController extends BaseController {
    @Resource
    private AdminConfig adminConfig;

    /**
     * 生成图片验证码
     *
     */
    @RequestMapping(value = "/checkCode")
    public void checkCode(HttpServletResponse response, HttpSession session) throws IOException {
        // 创建图片验证码对象
        CreateImageCode codeEntity = new CreateImageCode(130, 38, 5, 10);
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        // 将验证码存入session
        String code = codeEntity.getCode();
        session.setAttribute(Constants.CHECK_CODE_KEY, code);
        codeEntity.write(response.getOutputStream());
    }

    /**
     * 登录接口
     *
     * @param account   账号名
     * @param password  密码
     * @param checkCode 图片验证码
     */
    @RequestMapping("/login")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO<SessionAdminUserDto> login(HttpSession session,
                                                 @VerifyParam(required = true) String account,
                                                 @VerifyParam(required = true) String password,
                                                 @VerifyParam(required = true) String checkCode) {
        try {
            if (!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY))) {
                throw new BusinessException("图片验证码不正确");
            }

            if (!adminConfig.getAdminAccount().equals(account) || !SecureUtil.md5(adminConfig.getAdminPassword()).equals(password)) {
                throw new BusinessException("账号或密码错误");
            }
            // 登录成功，创建session
            SessionAdminUserDto sessionAdminUserDto = new SessionAdminUserDto();
            sessionAdminUserDto.setAccount(account);
            session.setAttribute(Constants.SESSION_KEY, sessionAdminUserDto);
            return getSuccessResponseVO(sessionAdminUserDto);
        } finally {
            session.removeAttribute(Constants.CHECK_CODE_KEY);
        }
    }
}
