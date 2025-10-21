package com.beanmeat.milvus.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Milvus配置属性类
 */
@Data
@Component
@ConfigurationProperties(prefix = "milvus")
public class MilvusProperties {
    
    /**
     * Milvus服务器主机地址
     */
    private String host = "localhost";
    
    /**
     * Milvus服务器端口
     */
    private int port = 19530;
    
    /**
     * 数据库名称
     */
    private String database = "default";
    
    /**
     * 集合配置
     */
    private Collection collection = new Collection();
    
    @Data
    public static class Collection {
        /**
         * 集合名称
         */
        private String name = "beanmeat_vectors";
        
        /**
         * 集合描述
         */
        private String description = "Beanmeat向量数据集合";
        
        /**
         * 向量维度
         */
        private int dimension = 1024;
        
        /**
         * 距离度量类型
         */
        private String metricType = "COSINE";
    }
}
