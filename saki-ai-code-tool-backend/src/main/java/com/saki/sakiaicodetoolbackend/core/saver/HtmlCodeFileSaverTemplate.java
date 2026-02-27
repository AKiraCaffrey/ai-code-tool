package com.saki.sakiaicodetoolbackend.core.saver;

import cn.hutool.core.util.StrUtil;
import com.saki.sakiaicodetoolbackend.ai.model.HtmlCodeResult;
import com.saki.sakiaicodetoolbackend.exception.BusinessException;
import com.saki.sakiaicodetoolbackend.exception.ErrorCode;
import com.saki.sakiaicodetoolbackend.model.enums.CodeGenTypeEnum;

/**
 * HTML代码文件保存器
 * <p>
 * 将AI生成的HTML代码保存为index.html文件
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-02-26
 */
public class HtmlCodeFileSaverTemplate extends CodeFileSaverTemplate<HtmlCodeResult> {


    @Override
    public CodeGenTypeEnum supportType() {
        return CodeGenTypeEnum.HTML;
    }

    @Override
    protected HtmlCodeResult cast(Object codeResult) {
        return (HtmlCodeResult) codeResult;
    }

    @Override
    protected void saveFiles(HtmlCodeResult result, String baseDirPath) {
        writeToFile(baseDirPath, "index.html", result.getHtmlCode());
    }

    @Override
    protected void validateInput(HtmlCodeResult result) {
        super.validateInput(result);
        if (StrUtil.isBlank(result.getHtmlCode())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "HTML 代码不能为空");
        }
    }
}
