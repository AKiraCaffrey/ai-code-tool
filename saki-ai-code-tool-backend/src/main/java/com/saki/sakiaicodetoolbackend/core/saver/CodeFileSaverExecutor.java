package com.saki.sakiaicodetoolbackend.core.saver;

import com.saki.sakiaicodetoolbackend.exception.BusinessException;
import com.saki.sakiaicodetoolbackend.exception.ErrorCode;
import com.saki.sakiaicodetoolbackend.model.enums.CodeGenTypeEnum;

import java.io.File;
import java.util.EnumMap;
import java.util.Map;

/**
 * 代码文件保存执行器
 * 根据代码生成类型执行相应的保存逻辑
 * @author Neal Caffrey
 * @version 1.0
 * @since 2025-12-17
 */
public class CodeFileSaverExecutor {

    /**
     * 保存器注册表
     */
    private static final Map<CodeGenTypeEnum, CodeFileSaver> SAVER_MAP =
            new EnumMap<>(CodeGenTypeEnum.class);

    static {
        register(new HtmlCodeFileSaverTemplate());
        register(new MultiFileCodeFileSaverTemplate());
    }

    /**
     * 注册保存器
     */
    private static void register(CodeFileSaver saver) {
        SAVER_MAP.put(saver.supportType(), saver);
    }

    /**
     * 执行代码保存
     *
     * @param codeResult  代码结果对象
     * @param codeGenType 代码生成类型
     * @param appId       应用 ID
     * @return 保存的目录
     */
    public static File executeSaver(Object codeResult, CodeGenTypeEnum codeGenType, Long appId) {
        CodeFileSaver saver = SAVER_MAP.get(codeGenType);
        if (saver == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成类型: " + codeGenType);
        }
        return saver.save(codeResult, appId);
    }
}
