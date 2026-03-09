package com.nlizzard.horizonhub.controller.basecontroller;

import com.nlizzard.horizonhub.enums.ResponseCodeEnum;

import com.nlizzard.horizonhub.entity.vo.ResponseVO;

public class ABaseController {

    //成功状态信息
    protected static final String STATUS_SUCCESS = "success";

    //错误状态信息
    protected static final String STATUS_ERROR = "error";

    /**
     *
     * @param t data数据
     * @return 一个成功的ResponseVO对象
     */
    protected <T> ResponseVO<T> getSuccessResponseVO(T t) {
        ResponseVO<T> responseVO = new ResponseVO<>();
        responseVO.setStatus(STATUS_SUCCESS);
        responseVO.setCode(ResponseCodeEnum.CODE_200.getCode());
        responseVO.setInfo(ResponseCodeEnum.CODE_200.getMsg());
        responseVO.setData(t);
        return responseVO;
    }
}
