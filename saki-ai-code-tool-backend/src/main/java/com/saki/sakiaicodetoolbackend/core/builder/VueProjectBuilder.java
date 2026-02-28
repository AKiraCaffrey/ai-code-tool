package com.saki.sakiaicodetoolbackend.core.builder;

import cn.hutool.core.util.RuntimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Vue项目构建器
 * <p>
 * 提供Vue项目的构建功能，支持异步构建和依赖安装
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-02-26
 */
@Slf4j
@Component
public class VueProjectBuilder {

    /**
     * 异步构建Vue项目
     *
     * @param projectPath 项目路径
     */
    public void buildProjectAsync(String projectPath) {
        Thread.ofVirtual().name("vue-builder-" + System.currentTimeMillis())
                .start(() -> buildProjectWithCallback(projectPath, status -> {}));
    }

    /**
     * 带状态回调的构建方法
     *
     * @param projectPath    项目路径
     * @param statusCallback 状态回调函数
     */
    public void buildProjectWithCallback(String projectPath, Consumer<BuildStatusEnum> statusCallback) {
        File projectDir = new File(projectPath);
        if (!validateProjectDir(projectDir, statusCallback)) {
            return;
        }

        log.info("开始构建 Vue 项目：{}", projectPath);

        statusCallback.accept(BuildStatusEnum.INSTALLING);
        if (!executeNpmInstall(projectDir)) {
            log.error("npm install 执行失败：{}", projectPath);
            statusCallback.accept(BuildStatusEnum.FAILED);
            return;
        }

        statusCallback.accept(BuildStatusEnum.BUILDING);
        if (!executeNpmBuild(projectDir)) {
            log.error("npm run build 执行失败：{}", projectPath);
            statusCallback.accept(BuildStatusEnum.FAILED);
            return;
        }

        if (!validateDistDir(projectDir, statusCallback)) {
            return;
        }

        log.info("Vue 项目构建成功，dist 目录：{}", projectPath);
        statusCallback.accept(BuildStatusEnum.COMPLETED);
    }

    /**
     * 同步构建Vue项目
     *
     * @param projectPath 项目根目录路径
     * @return 是否构建成功
     */
    public boolean buildProject(String projectPath) {
        AtomicBoolean success = new AtomicBoolean(false);
        buildProjectWithCallback(projectPath, status -> {
            success.set(status == BuildStatusEnum.COMPLETED);
        });
        return success.get();
    }

    /**
     * 验证项目目录
     *
     * @param projectDir     项目目录
     * @param statusCallback 状态回调函数
     * @return 是否验证通过
     */
    private boolean validateProjectDir(File projectDir, Consumer<BuildStatusEnum> statusCallback) {
        if (!projectDir.exists() || !projectDir.isDirectory()) {
            log.error("项目目录不存在：{}", projectDir.getAbsolutePath());
            statusCallback.accept(BuildStatusEnum.FAILED);
            return false;
        }
        File packageJsonFile = new File(projectDir, "package.json");
        if (!packageJsonFile.exists()) {
            log.error("项目目录中没有 package.json 文件：{}", projectDir.getAbsolutePath());
            statusCallback.accept(BuildStatusEnum.FAILED);
            return false;
        }
        return true;
    }

    /**
     * 验证构建输出目录
     *
     * @param projectDir     项目目录
     * @param statusCallback 状态回调函数
     * @return 是否验证通过
     */
    private boolean validateDistDir(File projectDir, Consumer<BuildStatusEnum> statusCallback) {
        File distDir = new File(projectDir, "dist");
        if (!distDir.exists() || !distDir.isDirectory()) {
            log.error("构建完成但 dist 目录未生成：{}", projectDir.getAbsolutePath());
            statusCallback.accept(BuildStatusEnum.FAILED);
            return false;
        }
        return true;
    }

    /**
     * 执行 npm install 命令
     *
     * @param projectDir 项目目录
     * @return 是否执行成功
     */
    private boolean executeNpmInstall(File projectDir) {
        log.info("执行 npm install...");
        return executeCommand(projectDir, buildCommand("npm") + " install", 300);
    }

    /**
     * 执行 npm run build 命令
     *
     * @param projectDir 项目目录
     * @return 是否执行成功
     */
    private boolean executeNpmBuild(File projectDir) {
        log.info("执行 npm run build...");
        return executeCommand(projectDir, buildCommand("npm") + " run build", 180);
    }

    /**
     * 根据操作系统构造命令
     *
     * @param baseCommand 基础命令
     * @return 完整命令
     */
    private String buildCommand(String baseCommand) {
        return isWindows() ? baseCommand + ".cmd" : baseCommand;
    }

    /**
     * 判断是否为Windows操作系统
     *
     * @return 是否为Windows
     */
    private boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    /**
     * 执行Shell命令
     *
     * @param workingDir     工作目录
     * @param command        命令字符串
     * @param timeoutSeconds 超时时间（秒）
     * @return 是否执行成功
     */
    private boolean executeCommand(File workingDir, String command, int timeoutSeconds) {
        try {
            log.info("在目录 {} 中执行命令: {}", workingDir.getAbsolutePath(), command);
            Process process = RuntimeUtil.exec(null, workingDir, command.split("\\s+"));
            boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
            if (!finished) {
                log.error("命令执行超时（{}秒），强制终止进程", timeoutSeconds);
                process.destroyForcibly();
                return false;
            }
            int exitCode = process.exitValue();
            if (exitCode == 0) {
                log.info("命令执行成功: {}", command);
                return true;
            } else {
                log.error("命令执行失败，退出码: {}", exitCode);
                return false;
            }
        } catch (Exception e) {
            log.error("执行命令失败: {}, 错误信息: {}", command, e.getMessage());
            return false;
        }
    }
}
