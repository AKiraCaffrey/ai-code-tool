package com.saki.sakiaicodetoolbackend.core.saver;

import com.saki.sakiaicodetoolbackend.model.enums.CodeGenTypeEnum;

import java.io.File;

/**
 * 策略接口
 * @author Neal Caffrey
 * @version 1.0
 * @since 2025-12-17
 */
public interface CodeFileSaver {
    /**
     * 当前保存器支持的代码生成类型
     */
    CodeGenTypeEnum supportType();

    /**
     * 执行代码保存
     *
     * @param codeResult 代码结果对象
     * @param appId 应用 ID
     * @return 保存目录
     */
    File save(Object codeResult, Long appId);
}
