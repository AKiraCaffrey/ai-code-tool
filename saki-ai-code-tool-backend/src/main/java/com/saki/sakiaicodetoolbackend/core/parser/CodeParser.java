package com.saki.sakiaicodetoolbackend.core.parser;

import com.saki.sakiaicodetoolbackend.model.enums.CodeGenTypeEnum;

/**
 * 代码解析器策略接口
 *
 * @author sakisaki
 */
public interface CodeParser<T> {

    /**
     * 当前解析器支持的类型
     */
    CodeGenTypeEnum supportType();
    /**
     * 解析代码内容
     *
     * @param codeContent 原始代码内容
     * @return 解析后的结果对象
     */
    T parseCode(String codeContent);
}
