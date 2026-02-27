package com.saki.sakiaicodetoolbackend.controller;

import com.saki.sakiaicodetoolbackend.langgraph4j.CodeGenWorkflow;
import com.saki.sakiaicodetoolbackend.langgraph4j.state.WorkflowContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

/**
 * 工作流SSE控制器
 * <p>
 * 提供LangGraph4j工作流的流式输出接口，支持同步、Flux和SSE三种执行方式
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-02-26
 */
@RestController
@RequestMapping("/workflow")
@Slf4j
public class WorkflowSseController {

    /**
     * 同步执行工作流
     *
     * @param prompt 提示词
     * @return 工作流执行结果
     */
    @PostMapping("/execute")
    public WorkflowContext executeWorkflow(@RequestParam String prompt) {
        log.info("收到同步工作流执行请求: {}", prompt);
        return new CodeGenWorkflow().executeWorkflow(prompt);
    }

    /**
     * Flux流式执行工作流
     *
     * @param prompt 提示词
     * @return Flux流式响应
     */
    @GetMapping(value = "/execute-flux", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> executeWorkflowWithFlux(@RequestParam String prompt) {
        log.info("收到 Flux 工作流执行请求: {}", prompt);
        return new CodeGenWorkflow().executeWorkflowWithFlux(prompt);
    }

    /**
     * SSE流式执行工作流
     *
     * @param prompt 提示词
     * @return SSE发射器
     */
    @GetMapping(value = "/execute-sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter executeWorkflowWithSse(@RequestParam String prompt) {
        log.info("收到 SSE 工作流执行请求: {}", prompt);
        return new CodeGenWorkflow().executeWorkflowWithSse(prompt);
    }
}