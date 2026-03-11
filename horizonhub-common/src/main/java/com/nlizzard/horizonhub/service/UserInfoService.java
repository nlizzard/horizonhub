package com.nlizzard.horizonhub.service;

import com.nlizzard.horizonhub.entity.dto.SessionWebUserDto;
import com.nlizzard.horizonhub.entity.enums.UserIntegralOperTypeEnum;
import com.nlizzard.horizonhub.entity.pojo.UserInfo;
import com.nlizzard.horizonhub.entity.query.UserInfoQuery;
import com.nlizzard.horizonhub.entity.vo.PaginationResultVO;

import java.util.List;

/**
 * @Description:用户信息Service
 * @author:nlizzard
 * @date:2026/03/08
 */
public interface UserInfoService {

    /**
     * 根据条件查询列表
     */
    List<UserInfo> findListByParam(UserInfoQuery query);

    /**
     * 根据条件查询数量
     */
    Integer findCountByParam(UserInfoQuery query);

    /**
     * 分页查询
     */
    PaginationResultVO<UserInfo> findListByPage(UserInfoQuery query);

    /**
     * 新增
     */
    Integer add(UserInfo bean);

    /**
     * 批量新增
     */
    Integer addBatch(List<UserInfo> listBean);

    /**
     * 批量新增或修改
     */
    Integer addOrUpdateBatch(List<UserInfo> listBean);

    /**
     * 根据UserId查询
     */
    UserInfo getUserInfoByUserId(String userId);

    /**
     * 根据UserId更新
     */
    Integer updateUserInfoByUserId(UserInfo bean, String userId);

    /**
     * 根据UserId删除
     */
    Integer deleteUserInfoByUserId(String userId);

    /**
     * 根据Email查询
     */
    UserInfo getUserInfoByEmail(String email);

    /**
     * 根据Email更新
     */
    Integer updateUserInfoByEmail(UserInfo bean, String email);

    /**
     * 根据Email删除
     */
    Integer deleteUserInfoByEmail(String email);

    /**
     * 根据NickName查询
     */
    UserInfo getUserInfoByNickName(String nickName);

    /**
     * 根据NickName更新
     */
    Integer updateUserInfoByNickName(UserInfo bean, String nickName);

    /**
     * 根据NickName删除
     */
    Integer deleteUserInfoByNickName(String nickName);

    /**
     * 注册账号接口
     *
     * @param email     邮箱
     * @param nickName  昵称
     * @param password  密码
     * @param emailCode 邮箱验证码
     */
    void register(String email, String nickName, String password, String emailCode);

    /**
     * 更新用户积分
     *
     * @param userId       用户ID
     * @param operTypeEnum 操作类型枚举
     * @param changeType   增加或减少类型，1-增加，-1-减少
     * @param integral     变更的积分数值
     */
    void updateUserIntegral(String userId, UserIntegralOperTypeEnum operTypeEnum, Integer changeType, Integer integral);

    /**
     * 登录接口
     *
     * @param email     邮箱
     * @param password  密码
     * @param ipAddress 登录 IP 地址
     * @return
     */
    SessionWebUserDto login(String email, String password, String ipAddress);

    /**
     * 重置密码接口
     *
     * @param email
     * @param password
     * @param emailCode
     */
    void resetPwd(String email, String password, String emailCode);
}