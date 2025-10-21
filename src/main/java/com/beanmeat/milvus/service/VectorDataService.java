package com.beanmeat.milvus.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.beanmeat.milvus.dto.*;
import com.beanmeat.milvus.entity.VectorData;
import com.beanmeat.milvus.repository.HttpUtil;
import com.beanmeat.milvus.repository.MilvusRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.System.currentTimeMillis;

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
    public VectorDTO createVectorData(VectorDTO request) {
        try {
            JsonObject jsonObject = new JsonObject();
            // 手动映射每个字段
            jsonObject.addProperty("id", currentTimeMillis());
            jsonObject.addProperty("description", request.getDescription());
            jsonObject.addProperty("segment", request.getSegment());
            jsonObject.add("description_vector", new Gson().toJsonTree(getVector(request.getDescription())));
            // 保存到Milvus
            milvusRepository.insert(jsonObject);
        } catch (Exception e) {
            log.error("创建向量数据失败", e);
            throw new RuntimeException("创建向量数据失败", e);
        }
        return request;
    }
    
    public static List<Float> getVector(String content) {
        HashMap<String, Object> param = new HashMap<>();
        param.put("model", "bge-m3");
        param.put("prompt", content);
        String json = JSON.toJSONString(param);
        String result = HttpUtil.postJson("http://192.168.1.106:11434/api/embeddings", json);
        System.out.println(content);
        System.out.println(result);
        return JSON.parseArray(JSON.parseObject(result).get("embedding").toString(), Float.class);
    }

//
//    /**
//     * 批量创建向量数据
//     */
//    @Transactional
//    public List<VectorDataResponse> createVectorDataBatch(List<VectorDataCreateRequest> requests) {
//        try {
//            long baseId = System.currentTimeMillis();
//
//            List<VectorData> vectorDataList = requests.stream()
//                    .map((request, index) -> {
//                        Long id = baseId + index;
//                        return VectorData.builder()
//                                .id(id)
//                                .description(request.getDescription())
//                                .segment(request.getSegment())
//                                .descriptionVector(request.getDescriptionVector())
//                                .createTime(LocalDateTime.now())
//                                .updateTime(LocalDateTime.now())
//                                .build();
//                    })
//                    .collect(Collectors.toList());
//
//            // 批量保存到Milvus
//            milvusRepository.insertBatch(vectorDataList);
//
//            log.info("批量创建向量数据成功, 数量: {}", vectorDataList.size());
//
//            // 转换为响应DTO列表
//            return vectorDataList.stream()
//                    .map(this::convertToResponse)
//                    .collect(Collectors.toList());
//
//        } catch (Exception e) {
//            log.error("批量创建向量数据失败", e);
//            throw new RuntimeException("批量创建向量数据失败", e);
//        }
//    }
//
    /**
     * 根据segment获取向量数据
     */
    public List<JSONObject> getVectorDataBySegment(Long segment) {
        try {
            List<JSONObject> ret = milvusRepository.findBySegment(segment);
            if (ret == null) {
                throw new RuntimeException("向量数据不存在, ID: " + segment);
            }
            return ret;
        } catch (Exception e) {
            log.error("获取向量数据失败, ID: {}", segment, e);
            throw new RuntimeException("获取向量数据失败", e);
        }
    }

    /**
     * 更新向量数据
     */
    public void updateVectorData(VectorDTO request) {
        try {
            // 先查询现有数据
            JSONObject existingData = milvusRepository.findById(String.valueOf(request.getId()));
            if (existingData == null) {
                throw new RuntimeException("向量数据不存在, ID: " + request.getId());
            }

            // 删除旧数据
            milvusRepository.deleteById(String.valueOf(request.getId()));

            // 创建更新后的数据
            JsonObject jsonObject = new JsonObject();
            // 手动映射每个字段
            jsonObject.addProperty("id", String.valueOf(request.getId()));
            jsonObject.addProperty("description", request.getDescription() != null ? request.getDescription() : existingData.getString("description"));
            jsonObject.addProperty("segment", request.getSegment() != null ? request.getSegment() : existingData.getInteger("segment"));
            jsonObject.add("description_vector", new Gson().toJsonTree(getVector(request.getDescription() != null ? request.getDescription() : existingData.getString("description"))));
            // 保存到Milvus
            milvusRepository.insert(jsonObject);
            log.info("向量数据更新成功, ID: {}", request.getId());
        } catch (Exception e) {
            log.error("更新向量数据失败, ID: {}", request.getId(), e);
            throw new RuntimeException("更新向量数据失败", e);
        }
    }

    /**
     * 删除向量数据
     */
    public void deleteVectorData(String ids) {
        try {
            milvusRepository.deleteById(ids);
        } catch (Exception e) {
            log.error("删除向量数据失败, ID: {}", ids, e);
            throw new RuntimeException("删除向量数据失败", e);
        }
    }
    
    /**
     * 向量相似度搜索
     */
    public List<JSONObject> searchVectorData(VectorSearchRequest request) {
        try {
            List<JSONObject> searchResults = milvusRepository.search(
                    request.getQueryText(),
                    request.getTopK()
            );
            return searchResults;
        } catch (Exception e) {
            log.error("向量搜索失败", e);
            throw new RuntimeException("向量搜索失败", e);
        }
    }
}
