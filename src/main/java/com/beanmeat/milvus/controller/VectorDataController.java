package com.beanmeat.milvus.controller;

import com.alibaba.fastjson2.JSONObject;
import com.beanmeat.milvus.dto.*;
import com.beanmeat.milvus.service.VectorDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 向量数据REST API控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/vectors")
@RequiredArgsConstructor
@Tag(name = "向量数据管理", description = "向量数据的增删改查和搜索API")
public class VectorDataController {
    
    private final VectorDataService vectorDataService;
    
    /**
     * 初始化集合
     */
    @PostMapping("/init")
    @Operation(summary = "初始化集合", description = "创建Milvus集合和索引")
    public ResponseEntity<ApiResponse<Void>> initializeCollection() {
        try {
            vectorDataService.initializeCollection();
            return ResponseEntity.ok(ApiResponse.success());
        } catch (Exception e) {
            log.error("初始化集合失败", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("初始化集合失败: " + e.getMessage()));
        }
    }
    
    /**
     * 创建向量数据
     */
    @PostMapping
    @Operation(summary = "创建向量数据", description = "创建单个向量数据")
    public ResponseEntity<ApiResponse<VectorDTO>> createVectorData(
            @Valid @RequestBody VectorDTO request) {
        try {
            vectorDataService.createVectorData(request);
            return ResponseEntity.ok(ApiResponse.success());
        } catch (Exception e) {
            log.error("创建向量数据失败", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("创建向量数据失败: " + e.getMessage()));
        }
    }

    /**
     * 删除向量数据
     */
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除向量数据", description = "根据ID删除向量数据")
    public ResponseEntity<ApiResponse<Void>> deleteVectorData(
            @Parameter(description = "向量数据ID") @PathVariable String ids) {
        try {
            vectorDataService.deleteVectorData(ids);
            return ResponseEntity.ok(ApiResponse.success());
        } catch (Exception e) {
            log.error("删除向量数据失败, ID: {}", ids, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("删除向量数据失败: " + e.getMessage()));
        }
    }

    /**
     * 更新向量数据
     */
    @PutMapping
    @Operation(summary = "更新向量数据", description = "更新向量数据")
    public ResponseEntity<ApiResponse<Void>> updateVectorData(
            @Valid @RequestBody VectorDTO request) {
        try {
            vectorDataService.updateVectorData(request);
            return ResponseEntity.ok(ApiResponse.success());
        } catch (Exception e) {
            log.error("更新向量数据失败, ID: {}", request.getId(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("更新向量数据失败: " + e.getMessage()));
        }
    }

    /**
     * 根据segment获取向量数据
     */
    @GetMapping("/{segment}")
    @Operation(summary = "获取向量数据", description = "根据ID获取向量数据")
    public ResponseEntity<ApiResponse<List<JSONObject>>> getVectorDataBySegment(
            @Parameter(description = "向量数据ID") @PathVariable Long segment) {
        try {
            List<JSONObject> response = vectorDataService.getVectorDataBySegment(segment);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            log.error("获取向量数据失败, ID: {}", segment, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("获取向量数据失败: " + e.getMessage()));
        }
    }

    /**
     * 向量相似度搜索
     */
    @PostMapping("/search")
    @Operation(summary = "向量搜索", description = "基于向量相似度进行搜索")
    public ResponseEntity<ApiResponse<List<JSONObject>>> searchVectorData(
            @Valid @RequestBody VectorSearchRequest request) {
        try {
            List<JSONObject> responses = vectorDataService.searchVectorData(request);
            return ResponseEntity.ok(ApiResponse.success(responses));
        } catch (Exception e) {
            log.error("向量搜索失败", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("向量搜索失败: " + e.getMessage()));
        }
    }

}
