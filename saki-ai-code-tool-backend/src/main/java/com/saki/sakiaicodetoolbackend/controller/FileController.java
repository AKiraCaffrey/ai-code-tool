package com.saki.sakiaicodetoolbackend.controller;

import com.saki.sakiaicodetoolbackend.common.BaseResponse;
import com.saki.sakiaicodetoolbackend.common.ResultUtils;
import com.saki.sakiaicodetoolbackend.exception.ErrorCode;
import com.saki.sakiaicodetoolbackend.exception.ThrowUtils;
import com.saki.sakiaicodetoolbackend.manager.CosManager;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件控制器
 * <p>
 * 提供文件上传接口
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-03-04
 */
@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private CosManager cosManager;

    /**
     * 上传图片
     * <p>
     * 将图片上传到腾讯云 COS，返回图片访问 URL
     * 支持的格式：jpg/png/gif/webp，最大 5MB
     *
     * @param file 图片文件
     * @return 图片访问 URL
     */
    @PostMapping("/upload")
    public BaseResponse<String> uploadImage(@RequestPart("file") MultipartFile file) {
        ThrowUtils.throwIf(file == null || file.isEmpty(), ErrorCode.PARAMS_ERROR, "文件不能为空");

        try {
            String url = cosManager.uploadImage(file);
            ThrowUtils.throwIf(url == null, ErrorCode.OPERATION_ERROR, "图片上传失败");
            return ResultUtils.success(url);
        } catch (IllegalArgumentException e) {
            log.warn("图片上传参数错误：{}", e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        } catch (Exception e) {
            log.error("图片上传失败", e);
            throw new RuntimeException("图片上传失败：" + e.getMessage());
        }
    }
}
