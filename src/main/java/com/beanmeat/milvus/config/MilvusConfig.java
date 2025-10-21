package com.beanmeat.milvus.config;

import io.milvus.v2.client.ConnectConfig;
import io.milvus.v2.client.MilvusClientV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Milvus连接配置类
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class MilvusConfig {
    
    private final MilvusProperties milvusProperties;
    
    /**
     * 创建Milvus V2客户端Bean
     */
    @Bean
    public MilvusClientV2 milvusClient() {
        try {
            ConnectConfig config = ConnectConfig.builder()
                    .uri(milvusProperties.getUri())
                    .build();
            
            MilvusClientV2 milvusClient = new MilvusClientV2(config);
            
            log.info("Milvus V2客户端连接成功: {}", milvusProperties.getUri());
            
            return milvusClient;
        } catch (Exception e) {
            log.error("Milvus V2客户端连接失败", e);
            throw new RuntimeException("无法连接到Milvus服务器", e);
        }
    }
}