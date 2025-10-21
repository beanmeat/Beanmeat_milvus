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
     * Milvus服务器URI
     */
    private String uri;
    
    /**
     * 集合配置
     */
    private Collection collection = new Collection();
    
    @Data
    public static class Collection {
        /**
         * 集合名称
         */
        private String name;
        
        /**
         * 集合描述
         */
        private String description;
        
        /**
         * 一致性级别
         */
        private String consistencyLevel;
        
        /**
         * 字段配置
         */
        private Fields fields = new Fields();
    }
    
    @Data
    public static class Fields {
        /**
         * ID字段配置
         */
        private FieldConfig id = new FieldConfig();
        
        /**
         * 描述字段配置
         */
        private FieldConfig description = new FieldConfig();
        
        /**
         * 段落字段配置
         */
        private FieldConfig segment = new FieldConfig();
        
        /**
         * 向量字段配置
         */
        private VectorFieldConfig descriptionVector = new VectorFieldConfig();
    }
    
    @Data
    public static class FieldConfig {
        /**
         * 数据类型
         */
        private String dataType;
        
        /**
         * 是否为主键
         */
        private Boolean isPrimaryKey = false;
        
        /**
         * 最大长度
         */
        private Integer maxLength;
    }
    
    @Data
    public static class VectorFieldConfig extends FieldConfig {
        /**
         * 向量维度
         */
        private Integer dimension;
        
        /**
         * 索引类型
         */
        private String indexType;
        
        /**
         * 度量类型
         */
        private String metricType;
    }
}