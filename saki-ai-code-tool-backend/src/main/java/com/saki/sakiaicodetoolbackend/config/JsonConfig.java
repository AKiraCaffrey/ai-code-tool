package com.saki.sakiaicodetoolbackend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * JSON配置类
 * <p>
 * 配置Jackson JSON序列化器，解决Long类型精度丢失问题
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-02-26
 */
@JsonComponent
public class JsonConfig {

    /**
     * 配置ObjectMapper
     * <p>
     * 添加Long转String的序列化器，解决前端JavaScript精度丢失问题
     *
     * @param builder Jackson对象映射器构建器
     * @return 配置好的ObjectMapper实例
     */
    @Bean
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Long.class, ToStringSerializer.instance);
        module.addSerializer(Long.TYPE, ToStringSerializer.instance);
        objectMapper.registerModule(module);
        return objectMapper;
    }
}