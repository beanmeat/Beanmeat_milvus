package com.beanmeat.milvus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分页响应")
public class PageResponse<T> {
    
    @Schema(description = "数据列表")
    private List<T> data;
    
    @Schema(description = "总数量", example = "100")
    private Long total;
    
    @Schema(description = "当前页码", example = "1")
    private Integer page;
    
    @Schema(description = "每页大小", example = "10")
    private Integer size;
    
    @Schema(description = "总页数", example = "10")
    private Integer totalPages;
    
    @Schema(description = "是否有下一页", example = "true")
    private Boolean hasNext;
    
    @Schema(description = "是否有上一页", example = "false")
    private Boolean hasPrevious;
}
