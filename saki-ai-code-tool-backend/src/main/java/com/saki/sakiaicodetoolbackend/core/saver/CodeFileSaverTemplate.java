package com.saki.sakiaicodetoolbackend.core.saver;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.saki.sakiaicodetoolbackend.constant.AppConstant;
import com.saki.sakiaicodetoolbackend.exception.BusinessException;
import com.saki.sakiaicodetoolbackend.exception.ErrorCode;
import com.saki.sakiaicodetoolbackend.model.enums.CodeGenTypeEnum;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * 抽象代码文件保存器
 * <p>
 * 使用模板方法模式定义代码文件保存的通用流程
 *
 * @param <T> 代码结果类型
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-02-26
 */
public abstract class CodeFileSaverTemplate<T> implements CodeFileSaver {

    /**
     * 文件保存的根目录
     */
    private static final String FILE_SAVE_ROOT_DIR = AppConstant.CODE_OUTPUT_ROOT_DIR;

    /**
     * 对外统一入口（策略接口方法）
     */
    @Override
    public final File save(Object codeResult, Long appId) {
        T result = cast(codeResult);
        return saveCode(result, appId);
    }


    /**
     * 模板方法：保存代码的标准流程
     *
     * @param result 代码结果对象
     * @param appId 应用 ID
     * @return 保存的目录
     */
    public final File saveCode(T result, Long appId) {
        // 1. 验证输入
        validateInput(result);
        // 2. 构建唯一目录
        String baseDirPath = buildUniqueDir(appId);
        // 3. 保存文件（具体实现交给子类）
        saveFiles(result, baseDirPath);
        // 4. 返回文件目录对象
        return new File(baseDirPath);
    }

    /**
     * 子类负责类型转换（强转风险内聚）
     */
    protected abstract T cast(Object codeResult);

    /**
     * 写入单个文件的工具方法
     *
     * @param dirPath  目录路径
     * @param filename 文件名
     * @param content  文件内容
     */
    public final void writeToFile(String dirPath, String filename, String content) {
        if (StrUtil.isNotBlank(content)) {
            String filePath = dirPath + File.separator + filename;
            FileUtil.writeString(content, filePath, StandardCharsets.UTF_8);
        }
    }

    /**
     * 输入校验（可被子类增强）
     * 验证输入参数
     *
     * @param result 代码结果对象
     */
    protected void validateInput(T result) {
        if (result == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "代码结果对象不能为空");
        }
    }

    /**
     * 构建唯一目录
     * 构建文件的唯一路径：tmp/code_output/bizType_雪花 ID
     *
     * @param appId 应用 ID
     * @return 目录路径
     */
    protected String buildUniqueDir(Long appId) {
        if (appId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "应用 ID 不能为空");
        }
        String codeType = supportType().getValue();
        String uniqueDirName = StrUtil.format("{}_{}", codeType, appId);
        String dirPath = FILE_SAVE_ROOT_DIR + File.separator + uniqueDirName;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }

    /**
     * 保存文件（具体实现交给子类）
     *
     * @param result      代码结果对象
     * @param baseDirPath 基础目录路径
     */
    protected abstract void saveFiles(T result, String baseDirPath);

}
