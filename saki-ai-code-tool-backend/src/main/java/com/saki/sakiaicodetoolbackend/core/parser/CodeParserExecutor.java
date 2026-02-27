package com.saki.sakiaicodetoolbackend.core.parser;

import com.saki.sakiaicodetoolbackend.exception.BusinessException;
import com.saki.sakiaicodetoolbackend.exception.ErrorCode;
import com.saki.sakiaicodetoolbackend.model.enums.CodeGenTypeEnum;

import java.util.EnumMap;
import java.util.Map;

/**
 * 代码解析执行器
 * 根据代码生成类型执行相应的解析逻辑
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2025-12-17
 */
public class CodeParserExecutor {

    private static final Map<CodeGenTypeEnum, CodeParser<?>> PARSER_MAP =
            new EnumMap<>(CodeGenTypeEnum.class);

    static {
        PARSER_MAP.put(CodeGenTypeEnum.HTML, new HtmlCodeParser());
        PARSER_MAP.put(CodeGenTypeEnum.MULTI_FILE, new MultiFileCodeParser());
    }

    /**
     * 执行代码解析
     *
     * @param codeContent     代码内容
     * @param codeGenTypeEnum 代码生成类型
     * @return 解析结果（HtmlCodeResult 或 MultiFileCodeResult）
     */
    public static Object executeParser(String codeContent, CodeGenTypeEnum codeGenTypeEnum) {
        CodeParser<?> parser = PARSER_MAP.get(codeGenTypeEnum);
        if (parser == null) {
            throw new BusinessException(
                    ErrorCode.SYSTEM_ERROR,
                    "不支持的代码生成类型：" + codeGenTypeEnum
            );
        }
        return parser.parseCode(codeContent);
    }
}
