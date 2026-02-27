package com.saki.sakiaicodetoolbackend.service;

import jakarta.servlet.http.HttpServletResponse;

/**
 * 项目下载服务接口
 * <p>
 * 提供项目代码打包下载功能
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-02-26
 */
public interface ProjectDownloadService {

    /**
     * 下载项目为压缩包
     * <p>
     * 将指定目录的项目代码打包为ZIP文件并下载
     *
     * @param projectPath      项目目录路径
     * @param downloadFileName 下载文件名
     * @param response         HTTP响应对象
     */
    void downloadProjectAsZip(String projectPath, String downloadFileName, HttpServletResponse response);
}
