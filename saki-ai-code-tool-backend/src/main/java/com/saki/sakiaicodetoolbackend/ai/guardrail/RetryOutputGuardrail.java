package com.saki.sakiaicodetoolbackend.ai.guardrail;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.guardrail.OutputGuardrail;
import dev.langchain4j.guardrail.OutputGuardrailResult;

/**
 * 重试输出护轨
 * <p>
 * 验证AI输出内容的有效性，对空内容、过短内容或敏感内容进行重试
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-02-26
 */
public class RetryOutputGuardrail implements OutputGuardrail {

    /**
     * 验证AI输出
     *
     * @param responseFromLLM LLM响应消息
     * @return 验证结果
     */
    @Override
    public OutputGuardrailResult validate(AiMessage responseFromLLM) {
        String response = responseFromLLM.text();
        // 检查响应是否为空或过短
        if (response == null || response.trim().isEmpty()) {
            return reprompt("响应内容为空", "请重新生成完整的内容");
        }
        if (response.trim().length() < 10) {
            return reprompt("响应内容过短", "请提供更详细的内容");
        }
        // 检查是否包含敏感信息或不当内容
        if (containsSensitiveContent(response)) {
            return reprompt("包含敏感信息", "请重新生成内容，避免包含敏感信息");
        }
        return success();
    }

    /**
     * 检查是否包含敏感内容
     *
     * @param response 响应内容
     * @return 是否包含敏感内容
     */
    private boolean containsSensitiveContent(String response) {
        String lowerResponse = response.toLowerCase();
        String[] sensitiveWords = {
                "密码", "password", "secret", "token",
                "api key", "私钥", "证书", "credential"
        };
        for (String word : sensitiveWords) {
            if (lowerResponse.contains(word)) {
                return true;
            }
        }
        return false;
    }
}