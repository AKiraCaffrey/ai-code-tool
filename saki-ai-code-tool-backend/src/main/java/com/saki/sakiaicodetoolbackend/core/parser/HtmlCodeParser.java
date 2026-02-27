package com.saki.sakiaicodetoolbackend.core.parser;

import com.saki.sakiaicodetoolbackend.ai.model.HtmlCodeResult;
import com.saki.sakiaicodetoolbackend.model.enums.CodeGenTypeEnum;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HTML单文件代码解析器
 * <p>
 * 解析AI生成的HTML代码，从Markdown代码块中提取HTML内容
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-02-26
 */
public class HtmlCodeParser implements CodeParser<HtmlCodeResult> {

    private static final Pattern HTML_CODE_PATTERN = Pattern.compile("```html\\s*\\n([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);

    @Override
    public CodeGenTypeEnum supportType() {
        return CodeGenTypeEnum.HTML;
    }

    @Override
    public HtmlCodeResult parseCode(String codeContent) {
        HtmlCodeResult result = new HtmlCodeResult();
        // 提取 HTML 代码
        String htmlCode = extractHtmlCode(codeContent);
        if (htmlCode != null && !htmlCode.trim().isEmpty()) {
            result.setHtmlCode(htmlCode.trim());
        } else {
            // 如果没有找到代码块，将整个内容作为HTML
            result.setHtmlCode(codeContent.trim());
        }
        return result;
    }

    /**
     * 提取 HTML 代码内容
     *
     * @param content 原始内容
     * @return HTML代码
     */
    private String extractHtmlCode(String content) {
        Matcher matcher = HTML_CODE_PATTERN.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}