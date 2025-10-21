package com.beanmeat.milvus.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 向量数据实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VectorData {
    
    /**
     * 主键ID
     */
    private String id;
    
    /**
     * 向量数据
     */
    private List<Float> vector;
    
    /**
     * 文本内容
     */
    private String text;
    
    /**
     * 元数据
     */
    private String metadata;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 距离分数（用于搜索结果）
     */
    private Float score;
}
