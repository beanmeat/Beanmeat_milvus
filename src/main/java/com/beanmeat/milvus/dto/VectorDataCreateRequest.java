package com.beanmeat.milvus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 向量数据创建请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "向量数据创建请求")
public class VectorDataCreateRequest {
    
    @NotBlank(message = "文本内容不能为空")
    @Schema(description = "文本内容", example = "这是一个示例文本")
    private String text;
    
    @Schema(description = "元数据", example = "{\"category\":\"example\",\"source\":\"manual\"}")
    private String metadata;
    
    @NotNull(message = "向量数据不能为空")
    @Schema(description = "向量数据", example = "[0.1, 0.2, 0.3, ...]")
    private List<Float> vector;
}
