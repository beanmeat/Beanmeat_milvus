package com.beanmeat.milvus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;

/**
 * 向量搜索请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "向量搜索请求")
public class VectorSearchRequest {
    
    @NotNull(message = "查询向量不能为空")
    @Schema(description = "查询向量", example = "[0.1, 0.2, 0.3, ...]")
    private List<Float> queryVector;
    
    @Min(value = 1, message = "返回数量必须大于0")
    @Max(value = 100, message = "返回数量不能超过100")
    @Schema(description = "返回结果数量", example = "10")
    private Integer topK = 10;
    
    @Min(value = 0, message = "相似度阈值不能小于0")
    @Max(value = 1, message = "相似度阈值不能大于1")
    @Schema(description = "相似度阈值", example = "0.7")
    private Double threshold = 0.7;
    
    @Schema(description = "过滤条件", example = "metadata.category == 'example'")
    private String filter;
}
