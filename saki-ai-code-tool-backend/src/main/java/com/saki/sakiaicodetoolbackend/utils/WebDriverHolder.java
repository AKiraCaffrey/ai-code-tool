package com.saki.sakiaicodetoolbackend.utils;

import com.saki.sakiaicodetoolbackend.exception.BusinessException;
import com.saki.sakiaicodetoolbackend.exception.ErrorCode;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

/**
 * WebDriver ThreadLocal 管理器：
 * - 每个线程/虚拟线程独享一个 WebDriver，避免并发互相覆盖页面
 * - 统一负责创建 / 获取 / 销毁，业务代码禁止直接 new ChromeDriver
 *
 * 说明：
 * - 不使用 synchronized / 连接池 / MQ
 * - 建议在每次截图完成后调用 {@link #cleanup()}，避免虚拟线程大量创建导致资源泄漏
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-02-25
 */
@Slf4j
public final class WebDriverHolder {

    private WebDriverHolder() {}

    private static final int DEFAULT_WIDTH = 1600;
    private static final int DEFAULT_HEIGHT = 900;

    private static final ThreadLocal<WebDriver> DRIVER_TL = new ThreadLocal<>();

    /**
     * 获取当前线程的 WebDriver（真正的懒加载）
     */
    public static WebDriver getDriver() {
        WebDriver driver = DRIVER_TL.get();

        if (driver == null) {
            driver = createChromeDriver(DEFAULT_WIDTH, DEFAULT_HEIGHT);
            DRIVER_TL.set(driver);
            log.debug("为线程 {} 创建新的 WebDriver", Thread.currentThread().getName());
        }

        return driver;
    }

    /**
     * 主动清理当前线程的 WebDriver。
     * - quit 释放浏览器进程资源
     * - remove 防止 ThreadLocal 泄漏（对虚拟线程尤其重要）
     */
    public static void cleanup() {
        WebDriver driver = DRIVER_TL.get();

        if (driver == null) {
            // 说明这个线程根本没用过 WebDriver
            return;
        }
        try {
            driver.quit();
        } catch (Exception e) {
            log.warn("关闭 WebDriver 失败（忽略继续）", e);
        } finally {
            DRIVER_TL.remove(); // 非常关键，尤其是虚拟线程
        }
    }

    /**
     * 集中创建 ChromeDriver（项目内唯一允许 new ChromeDriver 的地方）
     */
    private static WebDriver createChromeDriver(int width, int height) {
        try {
            // 自动管理 ChromeDriver
            WebDriverManager.chromedriver().setup();

            ChromeOptions options = new ChromeOptions();
            // 无头模式
            options.addArguments("--headless");
            // 禁用GPU（在某些环境下避免问题）
            options.addArguments("--disable-gpu");
            // 禁用沙盒模式（Docker环境需要）
            options.addArguments("--no-sandbox");
            // 禁用开发者shm使用
            options.addArguments("--disable-dev-shm-usage");
            // 设置窗口大小
            options.addArguments(String.format("--window-size=%d,%d", width, height));
            // 禁用扩展
            options.addArguments("--disable-extensions");
            // 设置用户代理
            options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");

            WebDriver driver = new ChromeDriver(options);
            // 设置页面加载超时
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
            // 设置隐式等待
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            return driver;
        } catch (Exception e) {
            log.error("初始化 Chrome 浏览器失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "初始化 Chrome 浏览器失败");
        }
    }
}

