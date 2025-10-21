package com.beanmeat.milvus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 向量数据DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "向量数据DTO")
public class VectorDTO {

    @Schema(description = "ID")
    private Long id;
    
    @Schema(description = "描述")
    private String description;
    
    @Schema(description = "段")
    private Integer segment;
}
