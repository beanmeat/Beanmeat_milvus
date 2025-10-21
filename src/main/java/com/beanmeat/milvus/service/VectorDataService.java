package com.beanmeat.milvus.service;

import com.beanmeat.milvus.dto.*;
import com.beanmeat.milvus.entity.VectorData;
import com.beanmeat.milvus.repository.MilvusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 向量数据业务逻辑层
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VectorDataService {
    
    private final MilvusRepository milvusRepository;
    
    /**
     * 初始化集合
     */
    public void initializeCollection() {
        try {
            milvusRepository.createCollection();
            log.info("向量数据集合初始化完成");
        } catch (Exception e) {
            log.error("向量数据集合初始化失败", e);
            throw new RuntimeException("向量数据集合初始化失败", e);
        }
    }
    
    /**
     * 创建向量数据
     */
    public VectorDataResponse createVectorData(VectorDataCreateRequest request) {
        try {
            // 生成唯一ID
            String id = UUID.randomUUID().toString();
            
            // 创建向量数据实体
            VectorData vectorData = VectorData.builder()
                    .id(id)
                    .text(request.getText())
                    .metadata(request.getMetadata())
                    .vector(request.getVector())
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();
            
            // 保存到Milvus
            milvusRepository.insert(vectorData);
            
            log.info("向量数据创建成功, ID: {}", id);
            
            // 转换为响应DTO
            return convertToResponse(vectorData);
            
        } catch (Exception e) {
            log.error("创建向量数据失败", e);
            throw new RuntimeException("创建向量数据失败", e);
        }
    }
    
    /**
     * 批量创建向量数据
     */
    public List<VectorDataResponse> createVectorDataBatch(List<VectorDataCreateRequest> requests) {
        try {
            List<VectorData> vectorDataList = requests.stream()
                    .map(request -> {
                        String id = UUID.randomUUID().toString();
                        return VectorData.builder()
                                .id(id)
                                .text(request.getText())
                                .metadata(request.getMetadata())
                                .vector(request.getVector())
                                .createTime(LocalDateTime.now())
                                .updateTime(LocalDateTime.now())
                                .build();
                    })
                    .collect(Collectors.toList());
            
            // 批量保存到Milvus
            milvusRepository.insertBatch(vectorDataList);
            
            log.info("批量创建向量数据成功, 数量: {}", vectorDataList.size());
            
            // 转换为响应DTO列表
            return vectorDataList.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
        } catch (Exception e) {
            log.error("批量创建向量数据失败", e);
            throw new RuntimeException("批量创建向量数据失败", e);
        }
    }
    
    /**
     * 根据ID获取向量数据
     */
    public VectorDataResponse getVectorDataById(String id) {
        try {
            VectorData vectorData = milvusRepository.findById(id);
            if (vectorData == null) {
                throw new RuntimeException("向量数据不存在, ID: " + id);
            }
            
            return convertToResponse(vectorData);
            
        } catch (Exception e) {
            log.error("获取向量数据失败, ID: {}", id, e);
            throw new RuntimeException("获取向量数据失败", e);
        }
    }
    
    /**
     * 更新向量数据
     */
    public VectorDataResponse updateVectorData(VectorDataUpdateRequest request) {
        try {
            // 先查询现有数据
            VectorData existingData = milvusRepository.findById(request.getId());
            if (existingData == null) {
                throw new RuntimeException("向量数据不存在, ID: " + request.getId());
            }
            
            // 删除旧数据
            milvusRepository.deleteById(request.getId());
            
            // 创建更新后的数据
            VectorData updatedData = VectorData.builder()
                    .id(request.getId())
                    .text(request.getText() != null ? request.getText() : existingData.getText())
                    .metadata(request.getMetadata() != null ? request.getMetadata() : existingData.getMetadata())
                    .vector(request.getVector() != null ? request.getVector() : existingData.getVector())
                    .createTime(existingData.getCreateTime())
                    .updateTime(LocalDateTime.now())
                    .build();
            
            // 插入更新后的数据
            milvusRepository.insert(updatedData);
            
            log.info("向量数据更新成功, ID: {}", request.getId());
            
            return convertToResponse(updatedData);
            
        } catch (Exception e) {
            log.error("更新向量数据失败, ID: {}", request.getId(), e);
            throw new RuntimeException("更新向量数据失败", e);
        }
    }
    
    /**
     * 删除向量数据
     */
    public void deleteVectorData(String id) {
        try {
            VectorData vectorData = milvusRepository.findById(id);
            if (vectorData == null) {
                throw new RuntimeException("向量数据不存在, ID: " + id);
            }
            
            milvusRepository.deleteById(id);
            
            log.info("向量数据删除成功, ID: {}", id);
            
        } catch (Exception e) {
            log.error("删除向量数据失败, ID: {}", id, e);
            throw new RuntimeException("删除向量数据失败", e);
        }
    }
    
    /**
     * 向量相似度搜索
     */
    public List<VectorDataResponse> searchVectorData(VectorSearchRequest request) {
        try {
            List<VectorData> searchResults = milvusRepository.search(
                    request.getQueryVector(),
                    request.getTopK(),
                    request.getThreshold(),
                    request.getFilter()
            );
            
            log.info("向量搜索完成, 查询向量维度: {}, 返回结果数量: {}", 
                    request.getQueryVector().size(), searchResults.size());
            
            return searchResults.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
        } catch (Exception e) {
            log.error("向量搜索失败", e);
            throw new RuntimeException("向量搜索失败", e);
        }
    }
    
    /**
     * 分页查询向量数据
     */
    public PageResponse<VectorDataResponse> getVectorDataPage(int page, int size) {
        try {
            // 计算偏移量
            int offset = (page - 1) * size;
            
            // 查询数据
            List<VectorData> vectorDataList = milvusRepository.findAll(offset, size);
            
            // 获取总数量
            long total = milvusRepository.count();
            
            // 计算总页数
            int totalPages = (int) Math.ceil((double) total / size);
            
            // 转换为响应DTO
            List<VectorDataResponse> responseList = vectorDataList.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            return PageResponse.<VectorDataResponse>builder()
                    .data(responseList)
                    .total(total)
                    .page(page)
                    .size(size)
                    .totalPages(totalPages)
                    .hasNext(page < totalPages)
                    .hasPrevious(page > 1)
                    .build();
            
        } catch (Exception e) {
            log.error("分页查询向量数据失败", e);
            throw new RuntimeException("分页查询向量数据失败", e);
        }
    }
    
    /**
     * 获取向量数据统计信息
     */
    public ApiResponse<Object> getStatistics() {
        try {
            long totalCount = milvusRepository.count();
            
            return ApiResponse.success(Map.of(
                    "totalCount", totalCount,
                    "collectionName", "beanmeat_vectors",
                    "vectorDimension", 1024,
                    "metricType", "COSINE"
            ));
            
        } catch (Exception e) {
            log.error("获取统计信息失败", e);
            throw new RuntimeException("获取统计信息失败", e);
        }
    }
    
    /**
     * 将VectorData实体转换为响应DTO
     */
    private VectorDataResponse convertToResponse(VectorData vectorData) {
        return VectorDataResponse.builder()
                .id(vectorData.getId())
                .text(vectorData.getText())
                .metadata(vectorData.getMetadata())
                .vector(vectorData.getVector())
                .createTime(vectorData.getCreateTime())
                .updateTime(vectorData.getUpdateTime())
                .score(vectorData.getScore())
                .build();
    }
}
