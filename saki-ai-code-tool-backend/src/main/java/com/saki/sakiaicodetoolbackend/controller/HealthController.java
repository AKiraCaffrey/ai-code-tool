package com.saki.sakiaicodetoolbackend.controller;

import com.saki.sakiaicodetoolbackend.common.BaseResponse;
import com.saki.sakiaicodetoolbackend.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查控制器
 * <p>
 * 提供服务健康检查接口
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-02-26
 */
@RestController
@RequestMapping("/health")
public class HealthController {

    /**
     * 健康检查接口
     *
     * @return 健康状态
     */
    @GetMapping("/")
    public BaseResponse<String> healthCheck() {
        return ResultUtils.success("ok");
    }
}
