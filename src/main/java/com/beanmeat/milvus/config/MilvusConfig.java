package com.beanmeat.milvus.config;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
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
     * 创建Milvus客户端Bean
     */
    @Bean
    public MilvusServiceClient milvusClient() {
        try {
            ConnectParam connectParam = ConnectParam.newBuilder()
                    .withHost(milvusProperties.getHost())
                    .withPort(milvusProperties.getPort())
                    .withDatabaseName(milvusProperties.getDatabase())
                    .build();
            
            MilvusServiceClient milvusClient = new MilvusServiceClient(connectParam);
            
            log.info("Milvus客户端连接成功: {}:{}", 
                    milvusProperties.getHost(), 
                    milvusProperties.getPort());
            
            return milvusClient;
        } catch (Exception e) {
            log.error("Milvus客户端连接失败", e);
            throw new RuntimeException("无法连接到Milvus服务器", e);
        }
    }
}
