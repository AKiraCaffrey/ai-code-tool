package com.saki.sakiaicodetoolbackend.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.saki.sakiaicodetoolbackend.model.dto.app.AppAddRequest;
import com.saki.sakiaicodetoolbackend.model.dto.app.AppQueryRequest;
import com.saki.sakiaicodetoolbackend.model.entity.App;
import com.saki.sakiaicodetoolbackend.model.entity.User;
import com.saki.sakiaicodetoolbackend.model.vo.AppVO;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 应用服务接口
 * <p>
 * 提供应用的创建、部署、代码生成等核心业务功能
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-02-26
 */
public interface AppService extends IService<App> {

    /**
     * 通过对话生成应用代码
     * <p>
     * 使用AI对话方式生成应用代码，支持流式返回
     *
     * @param appId     应用ID
     * @param message   用户输入的提示词
     * @param loginUser 当前登录用户
     * @return 流式代码生成结果
     */
    Flux<String> chatToGenCode(Long appId, String message, User loginUser);

    /**
     * 创建应用
     *
     * @param appAddRequest 创建应用请求参数
     * @param loginUser     当前登录用户
     * @return 新创建的应用ID
     */
    Long createApp(AppAddRequest appAddRequest, User loginUser);

    /**
     * 应用部署
     * <p>
     * 将应用部署到服务器，返回可访问的部署地址
     *
     * @param appId     应用ID
     * @param loginUser 当前登录用户
     * @return 可访问的部署URL
     */
    String deployApp(Long appId, User loginUser);

    /**
     * 异步生成应用截图并更新封面
     *
     * @param appId  应用ID
     * @param appUrl 应用访问URL
     */
    void generateAppScreenshotAsync(Long appId, String appUrl);

    /**
     * 获取应用封装类
     *
     * @param app 应用实体
     * @return 应用视图对象
     */
    AppVO getAppVO(App app);

    /**
     * 分页获取精选应用列表
     *
     * @param appQueryRequest 查询请求参数
     * @return 精选应用分页列表
     */
    Page<AppVO> listGoodAppVOByPage(AppQueryRequest appQueryRequest);

    /**
     * 获取应用封装类列表
     *
     * @param appList 应用实体列表
     * @return 应用视图对象列表
     */
    List<AppVO> getAppVOList(List<App> appList);

    /**
     * 构造应用查询条件
     *
     * @param appQueryRequest 查询请求参数
     * @return 查询条件包装器
     */
    QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest);

}
