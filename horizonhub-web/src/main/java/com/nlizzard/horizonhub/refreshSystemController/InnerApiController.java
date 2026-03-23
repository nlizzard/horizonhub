package com.nlizzard.horizonhub.refreshSystemController;

import cn.hutool.crypto.SecureUtil;
import com.nlizzard.horizonhub.annotation.GlobalInterceptor;
import com.nlizzard.horizonhub.annotation.VerifyParam;
import com.nlizzard.horizonhub.basecontroller.BaseController;
import com.nlizzard.horizonhub.entity.config.WebConfig;
import com.nlizzard.horizonhub.entity.dto.SysSettingDto;
import com.nlizzard.horizonhub.entity.enums.ResponseCodeEnum;
import com.nlizzard.horizonhub.entity.vo.ResponseVO;
import com.nlizzard.horizonhub.exception.BusinessException;
import com.nlizzard.horizonhub.service.SysSettingService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/innerApi")
public class InnerApiController extends BaseController {

    @Resource
    private WebConfig webConfig;

    @Resource
    private SysSettingService sysSettingService;

    /**
     * 管理端修改系统设置后，调该内部接口刷新内存中的系统设置
     *
     * @param appKey
     * @param timestamp
     * @param sign
     * @return
     */
    @RequestMapping("/refresSysSetting")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO<SysSettingDto> refreshSysSetting(@VerifyParam(required = true) String appKey,
                                                       @VerifyParam(required = true) Long timestamp,
                                                       @VerifyParam(required = true) String sign) {
        // 校验appKey、timestamp、sign
        if (!webConfig.getInnerApiAppKey().equals(appKey)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        if (System.currentTimeMillis() - timestamp > 1000 * 10) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        // 生成签名
        String mySign = SecureUtil.md5(appKey + timestamp + webConfig.getInnerApiAppSecret());
        if (!mySign.equals(sign)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        // 刷新系统设置到内存
        return getSuccessResponseVO(sysSettingService.initSysSettingToCache());
    }
}
