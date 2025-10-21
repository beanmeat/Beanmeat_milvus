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
 * 向量数据更新请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "向量数据更新请求")
public class VectorDataUpdateRequest {
    
    @NotBlank(message = "ID不能为空")
    @Schema(description = "数据ID", example = "12345678-1234-1234-1234-123456789012")
    private String id;
    
    @Schema(description = "文本内容", example = "更新后的文本内容")
    private String text;
    
    @Schema(description = "元数据", example = "{\"category\":\"updated\",\"source\":\"api\"}")
    private String metadata;
    
    @Schema(description = "向量数据", example = "[0.4, 0.5, 0.6, ...]")
    private List<Float> vector;
}
