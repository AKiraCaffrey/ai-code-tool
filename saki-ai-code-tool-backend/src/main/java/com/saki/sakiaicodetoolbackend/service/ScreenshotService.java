package com.saki.sakiaicodetoolbackend.service;

/**
 * 截图服务接口
 * <p>
 * 提供网页截图生成和上传功能
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-02-26
 */
public interface ScreenshotService {

    /**
     * 通用的截图服务，生成截图并上传到对象存储
     *
     * @param webUrl 网页URL
     * @return 截图访问地址
     */
    String generateAndUploadScreenshot(String webUrl);

}
