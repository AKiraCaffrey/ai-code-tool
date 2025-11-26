package com.saki.sakiaicodetoolbackend;

import dev.langchain4j.community.store.embedding.redis.spring.RedisEmbeddingStoreAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication(exclude = {RedisEmbeddingStoreAutoConfiguration.class})
@MapperScan("com.saki.sakiaicodetoolbackend.mapper")
public class SaKiAiCodeToolApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaKiAiCodeToolApplication.class, args);
    }

}
