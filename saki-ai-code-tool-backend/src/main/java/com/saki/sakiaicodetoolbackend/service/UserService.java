package com.saki.sakiaicodetoolbackend.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.saki.sakiaicodetoolbackend.model.dto.user.UserQueryRequest;
import com.saki.sakiaicodetoolbackend.model.dto.user.UserUpdateMyRequest;
import com.saki.sakiaicodetoolbackend.model.entity.User;
import com.saki.sakiaicodetoolbackend.model.vo.LoginUserVO;
import com.saki.sakiaicodetoolbackend.model.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 用户服务接口
 * <p>
 * 提供用户注册、登录、注销及用户信息管理功能
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-02-26
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户ID
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 获取脱敏的已登录用户信息
     *
     * @param user 用户实体
     * @return 脱敏后的登录用户信息
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request      HTTP请求对象
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request HTTP请求对象
     * @return 当前登录用户实体
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获取脱敏后的用户信息
     *
     * @param user 用户实体
     * @return 脱敏后的用户视图对象
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏后的用户信息列表
     *
     * @param userList 用户实体列表
     * @return 脱敏后的用户视图对象列表
     */
    List<UserVO> getUserVOList(List<User> userList);

    /**
     * 用户注销
     *
     * @param request HTTP请求对象
     * @return 注销是否成功
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 根据查询条件构造数据查询参数
     *
     * @param userQueryRequest 查询请求参数
     * @return 查询条件包装器
     */
    QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 加密用户密码
     *
     * @param userPassword 用户密码
     * @return 加密后的用户密码
     */
    String getEncryptPassword(String userPassword);

    /**
     * 更新当前用户信息
     *
     * @param request     更新请求参数
     * @param loginUserId 当前登录用户ID
     * @return 是否更新成功
     */
    boolean updateMyUser(UserUpdateMyRequest request, Long loginUserId);

    /**
     * 获取当前用户创建的应用列表
     *
     * @param loginUserId 当前登录用户ID
     * @return 应用列表
     */
    List<com.saki.sakiaicodetoolbackend.model.vo.AppVO> getMyApps(Long loginUserId);

    /**
     * 获取当前用户发布的帖子列表
     *
     * @param loginUserId 当前登录用户ID
     * @return 帖子列表
     */
    List<com.saki.sakiaicodetoolbackend.model.vo.PostVO> getMyPosts(Long loginUserId);

    /**
     * 获取当前用户点赞的帖子列表
     *
     * @param loginUserId 当前登录用户ID
     * @return 帖子列表
     */
    List<com.saki.sakiaicodetoolbackend.model.vo.PostVO> getMyLikedPosts(Long loginUserId);
}
