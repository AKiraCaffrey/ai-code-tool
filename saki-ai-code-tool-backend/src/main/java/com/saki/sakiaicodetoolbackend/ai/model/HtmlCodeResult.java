package com.saki.sakiaicodetoolbackend.ai.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

/**
 * HTML 代码结果
 * <p>
 * 用于存储AI生成的HTML代码及其描述信息
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-03-01
 */
@Description("生成 HTML 代码文件的结果")
@Data
public class HtmlCodeResult {

    /**
     * HTML 代码
     */
    @Description("HTML代码")
    private String htmlCode;

    /**
     * 描述
     */
    @Description("生成代码的描述")
    private String description;
}