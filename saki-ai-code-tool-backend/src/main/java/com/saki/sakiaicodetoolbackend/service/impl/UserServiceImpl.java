package com.saki.sakiaicodetoolbackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.saki.sakiaicodetoolbackend.exception.BusinessException;
import com.saki.sakiaicodetoolbackend.exception.ErrorCode;
import com.saki.sakiaicodetoolbackend.exception.ThrowUtils;
import com.saki.sakiaicodetoolbackend.mapper.UserMapper;
import com.saki.sakiaicodetoolbackend.model.dto.user.UserQueryRequest;
import com.saki.sakiaicodetoolbackend.model.dto.user.UserUpdateMyRequest;
import com.saki.sakiaicodetoolbackend.model.entity.User;
import com.saki.sakiaicodetoolbackend.model.enums.UserRoleEnum;
import com.saki.sakiaicodetoolbackend.model.vo.AppVO;
import com.saki.sakiaicodetoolbackend.model.vo.LoginUserVO;
import com.saki.sakiaicodetoolbackend.model.vo.PostVO;
import com.saki.sakiaicodetoolbackend.model.vo.UserVO;
import com.saki.sakiaicodetoolbackend.service.UserService;
import com.saki.sakiaicodetoolbackend.mapper.AppMapper;
import com.saki.sakiaicodetoolbackend.mapper.PostMapper;
import com.saki.sakiaicodetoolbackend.mapper.PostLikeMapper;
import com.saki.sakiaicodetoolbackend.model.entity.App;
import com.saki.sakiaicodetoolbackend.model.entity.Post;
import com.saki.sakiaicodetoolbackend.model.entity.PostLike;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.saki.sakiaicodetoolbackend.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现类
 * <p>
 * 实现用户注册、登录、注销及用户信息管理功能
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-02-26
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验参数
        if (StrUtil.hasBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度过短");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        // 2. 查询用户是否已存在
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_account", userAccount);
        long count = this.mapper.selectCountByQuery(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }
        // 3. 加密密码
        String encryptPassword = getEncryptPassword(userPassword);
        // 4. 创建用户，插入数据库
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName("无名");
        user.setUserProfile("ZeroCode 冒险家，快来丰富你的个人介绍~");
        user.setUserRole(UserRoleEnum.USER.getValue());
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "注册失败，数据库错误");
        }
        return user.getId();
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtil.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验参数
        if (StrUtil.hasBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度过短");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度过短");
        }
        // 2. 加密
        String encryptPassword = getEncryptPassword(userPassword);
        // 3. 查询用户是否存在
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_account", userAccount);
        queryWrapper.eq("user_password", encryptPassword);
        User user = this.mapper.selectOneByQuery(queryWrapper);
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 4. 如果用户存在，记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        // 5. 返回脱敏的用户信息
        return this.getLoginUserVO(user);
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断用户是否登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询当前用户信息
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream()
                .map(this::getUserVO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        // 先判断用户是否登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        return QueryWrapper.create()
                .eq("id", id)
                .eq("user_role", userRole)
                .like("user_account", userAccount)
                .like("user_name", userName)
                .like("user_profile", userProfile)
                .orderBy(sortField, "ascend".equals(sortOrder));
    }

    @Override
    public String getEncryptPassword(String userPassword) {
        // 盐值，混淆密码
        final String SALT = "sakisaki";
        return DigestUtils.md5DigestAsHex((userPassword + SALT).getBytes(StandardCharsets.UTF_8));
    }

    @Resource
    private AppMapper appMapper;

    @Resource
    private PostMapper postMapper;

    @Resource
    private PostLikeMapper postLikeMapper;

    @Override
    public boolean updateMyUser(UserUpdateMyRequest request, Long loginUserId) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
        ThrowUtils.throwIf(loginUserId == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");

        User user = this.getById(loginUserId);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");

        if (StrUtil.isNotBlank(request.getUserName())) {
            user.setUserName(request.getUserName());
        }
        if (StrUtil.isNotBlank(request.getUserProfile())) {
            user.setUserProfile(request.getUserProfile());
        }
        if (StrUtil.isNotBlank(request.getUserAvatar())) {
            user.setUserAvatar(request.getUserAvatar());
        }
        return this.updateById(user);
    }

    @Override
    public List<AppVO> getMyApps(Long loginUserId) {
        ThrowUtils.throwIf(loginUserId == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");

        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq("user_id", loginUserId)
                .eq("is_delete", 0)
                .orderBy("create_time", false);
        List<App> appList = appMapper.selectListByQuery(queryWrapper);
        return getAppVOList(appList);
    }

    @Override
    public List<PostVO> getMyPosts(Long loginUserId) {
        ThrowUtils.throwIf(loginUserId == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq("user_id", loginUserId)
                .eq("is_delete", 0)
                .orderBy("create_time", false);
        List<Post> postList = postMapper.selectListByQuery(queryWrapper);
        return getPostVOList(postList, loginUserId);
    }

    @Override
    public List<PostVO> getMyLikedPosts(Long loginUserId) {
        ThrowUtils.throwIf(loginUserId == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        QueryWrapper likeQueryWrapper = QueryWrapper.create()
                .eq("user_id", loginUserId)
                .eq("is_delete", 0);
        List<PostLike> postLikeList = postLikeMapper.selectListByQuery(likeQueryWrapper);
        if (CollUtil.isEmpty(postLikeList)) {
            return new ArrayList<>();
        }
        List<Long> postIds = postLikeList.stream()
                .map(PostLike::getPostId)
                .collect(Collectors.toList());
        QueryWrapper postQueryWrapper = QueryWrapper.create()
                .in("id", postIds)
                .eq("is_delete", 0)
                .orderBy("create_time", false);
        List<Post> postList = postMapper.selectListByQuery(postQueryWrapper);
        return getPostVOList(postList, loginUserId);
    }

    private List<AppVO> getAppVOList(List<App> appList) {
        if (CollUtil.isEmpty(appList)) {
            return new ArrayList<>();
        }
        return appList.stream().map(app -> {
            AppVO appVO = new AppVO();
            BeanUtil.copyProperties(app, appVO);
            Long userId = app.getUserId();
            if (userId != null) {
                User user = this.getById(userId);
                UserVO userVO = getUserVO(user);
                appVO.setUser(userVO);
            }
            return appVO;
        }).collect(Collectors.toList());
    }

    private List<PostVO> getPostVOList(List<Post> postList, Long loginUserId) {
        if (CollUtil.isEmpty(postList)) {
            return new ArrayList<>();
        }
        return postList.stream().map(post -> {
            PostVO postVO = new PostVO();
            BeanUtil.copyProperties(post, postVO);
            Long userId = post.getUserId();
            if (userId != null) {
                User user = this.getById(userId);
                UserVO userVO = getUserVO(user);
                postVO.setUser(userVO);
            }
            if (loginUserId != null) {
                QueryWrapper likeQueryWrapper = QueryWrapper.create()
                    .eq("post_id", post.getId())
                    .eq("user_id", loginUserId);
                PostLike postLike = postLikeMapper.selectOneByQuery(likeQueryWrapper);
                postVO.setIsLiked(postLike != null);
            }
            return postVO;
        }).collect(Collectors.toList());
    }
}
