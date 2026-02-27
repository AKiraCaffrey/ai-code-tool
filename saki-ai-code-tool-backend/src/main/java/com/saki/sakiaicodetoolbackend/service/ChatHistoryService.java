package com.saki.sakiaicodetoolbackend.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.saki.sakiaicodetoolbackend.model.dto.chathistory.ChatHistoryQueryRequest;
import com.saki.sakiaicodetoolbackend.model.entity.ChatHistory;
import com.saki.sakiaicodetoolbackend.model.entity.User;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;

import java.time.LocalDateTime;

/**
 * 对话历史服务接口
 * <p>
 * 提供对话历史的增删改查及内存加载功能
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-02-26
 */
public interface ChatHistoryService extends IService<ChatHistory> {

    /**
     * 添加对话历史
     *
     * @param appId       应用ID
     * @param message     消息内容
     * @param messageType 消息类型
     * @param userId      用户ID
     * @return 是否添加成功
     */
    boolean addChatMessage(Long appId, String message, String messageType, Long userId);

    /**
     * 根据应用ID删除对话历史
     *
     * @param appId 应用ID
     * @return 是否删除成功
     */
    boolean deleteByAppId(Long appId);

    /**
     * 分页查询某应用的对话记录
     * <p>
     * 支持游标查询，根据创建时间倒序返回
     *
     * @param appId          应用ID
     * @param pageSize       页面大小
     * @param lastCreateTime 最后一条记录的创建时间（游标）
     * @param loginUser      当前登录用户
     * @return 对话历史分页数据
     */
    Page<ChatHistory> listAppChatHistoryByPage(Long appId, int pageSize,
                                               LocalDateTime lastCreateTime,
                                               User loginUser);

    /**
     * 加载对话历史到内存
     * <p>
     * 将对话历史加载到聊天内存中，用于AI对话上下文
     *
     * @param appId      应用ID
     * @param chatMemory 聊天内存对象
     * @param maxCount   最多加载条数
     * @return 加载成功的条数
     */
    int loadChatHistoryToMemory(Long appId, MessageWindowChatMemory chatMemory, int maxCount);

    /**
     * 构造查询条件
     *
     * @param chatHistoryQueryRequest 查询请求参数
     * @return 查询条件包装器
     */
    QueryWrapper getQueryWrapper(ChatHistoryQueryRequest chatHistoryQueryRequest);
}
