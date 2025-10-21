package com.beanmeat.milvus.entity;

import io.swagger.v3.oas.annotations.media.Schema;
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
     * 描述
     */
    private String description;

    /**
     * 段
     */
    private Integer segment;

    /**
     * 向量数据
     */
    private List<Float> descriptionVector;
}
