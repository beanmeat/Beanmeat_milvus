package com.beanmeat.milvus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 向量数据响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "向量数据响应")
public class VectorDataResponse {
    
    @Schema(description = "数据ID", example = "12345678-1234-1234-1234-123456789012")
    private String id;
    
    @Schema(description = "文本内容", example = "这是一个示例文本")
    private String text;
    
    @Schema(description = "元数据", example = "{\"category\":\"example\",\"source\":\"manual\"}")
    private String metadata;
    
    @Schema(description = "向量数据", example = "[0.1, 0.2, 0.3, ...]")
    private List<Float> vector;
    
    @Schema(description = "创建时间", example = "2024-01-01T10:00:00")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间", example = "2024-01-01T10:00:00")
    private LocalDateTime updateTime;
    
    @Schema(description = "相似度分数", example = "0.95")
    private Float score;
}
