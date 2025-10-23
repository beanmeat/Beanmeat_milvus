package com.beanmeat.milvus.repository;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.beanmeat.milvus.config.MilvusProperties;
import com.beanmeat.milvus.entity.VectorData;
import com.google.gson.JsonObject;
import io.milvus.grpc.QueryResults;
import io.milvus.param.R;
import io.milvus.param.dml.QueryParam;
import io.milvus.v2.client.MilvusClientV2;
import io.milvus.v2.common.ConsistencyLevel;
import io.milvus.v2.common.DataType;
import io.milvus.v2.common.IndexParam;
import io.milvus.v2.service.collection.request.*;
import io.milvus.v2.service.collection.response.*;
import io.milvus.v2.service.vector.request.*;
import io.milvus.v2.service.vector.request.data.FloatVec;
import io.milvus.v2.service.vector.response.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.StreamUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.beanmeat.milvus.service.VectorDataService.getVector;

/**
 * Milvus数据访问层 - 使用V2客户端
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class MilvusRepository {

    private String COLLECTION_NAME = "beanmeat_test";

    private final MilvusClientV2 milvusClient;
    private final MilvusProperties milvusProperties;

    /**
     * 创建集合
     */
    public void createCollection() {
        try {
            // 检查Milvus集合是否存在
            Boolean exist = milvusClient.hasCollection(
                    HasCollectionReq.builder()
                            .collectionName(COLLECTION_NAME)
                            .build()
            );

            if (exist) {
                log.info("集合 {} 已存在", COLLECTION_NAME);

                milvusClient.dropCollection(DropCollectionReq.builder()
                        .collectionName(COLLECTION_NAME)
                        .build());
                log.info("已删除现有集合: {}", COLLECTION_NAME);
            }
            
            // 创建集合Schema
            CreateCollectionReq.CollectionSchema schema = milvusClient.createSchema();
            
            // 添加ID字段
            schema.addField(AddFieldReq.builder()
                    .fieldName("id")
                    .dataType(DataType.Int64)
                    .isPrimaryKey(true)
                    .maxLength(64)
                    .build());

            schema.addField(AddFieldReq.builder()
                    .fieldName("description")
                    .dataType(DataType.VarChar)
                    .maxLength(1024)
                    .build());
            schema.addField(AddFieldReq.builder()
                    .fieldName("segment")
                    .dataType(DataType.Int64)
                    .maxLength(64)
                    .build());
            schema.addField(AddFieldReq.builder()
                    .fieldName("description_vector")
                    .dataType(DataType.FloatVector)
                    .dimension(1024)
                    .build());
            
            // 配置索引
            List<IndexParam> indexes = new ArrayList<>();
            indexes.add(IndexParam.builder()
                    .fieldName("description_vector")
                    .indexType(IndexParam.IndexType.FLAT)
                    .metricType(IndexParam.MetricType.COSINE)
                    .build());
            
            // 创建集合
            CreateCollectionReq requestCreate = CreateCollectionReq.builder()
                    .collectionName(COLLECTION_NAME)
                    .collectionSchema(schema)
                    .indexParams(indexes)
                    .consistencyLevel(ConsistencyLevel.STRONG)
                    .build();
            
            milvusClient.createCollection(requestCreate);
            log.info("集合 {} 创建成功", COLLECTION_NAME);
            
        } catch (Exception e) {
            log.error("创建集合时发生错误", e);
            throw new RuntimeException("创建集合失败", e);
        }
    }

    /**
     * 插入向量数据
     * @param vectorData
     */
    public void insert(JsonObject vectorData) {
        try {
            InsertReq insertReq = InsertReq.builder()
                    .collectionName(COLLECTION_NAME)
                    .data(Collections.singletonList(vectorData))
                    .build();
            
            milvusClient.insert(insertReq);
        } catch (Exception e) {
            log.error("插入向量数据时发生错误", e);
            throw new RuntimeException("插入向量数据失败", e);
        }
    }

    /**
     * 根据IDS删除向量数据
     * @param ids
     */
    public void deleteById(String ids) {
        try {
            DeleteReq deleteReq = DeleteReq.builder()
                    .collectionName(COLLECTION_NAME)
                    .ids(Arrays.asList(ids.split(",")).stream().map(Long::parseLong)
                            .collect(Collectors.toList()))
                    .build();
            
           milvusClient.delete(deleteReq);
        } catch (Exception e) {
            log.error("删除向量数据时发生错误", e);
            throw new RuntimeException("删除向量数据失败", e);
        }
    }


    /**
     * 根据segment查询向量数据
     * @param segment
     * @return
     */
    public List<JSONObject> findBySegment(Long segment) {
        try {
            String collectionName = milvusProperties.getCollection().getName();
            String expr = String.format("segment == %d", segment);

            QueryReq queryReq = QueryReq.builder()
                    .collectionName(collectionName)
                    .filter(expr)
                    .outputFields(Arrays.asList("id", "description", "segment", "description_vector"))
                    .build();

            QueryResp queryResp = milvusClient.query(queryReq);

            return queryResp.getQueryResults().size() > 0 ? queryResp.getQueryResults().stream().map(a -> JSONObject.from(a.getEntity())).collect(Collectors.toList()) : new ArrayList<>();

        } catch (Exception e) {
            log.error("查询向量数据时发生错误", e);
            throw new RuntimeException("查询向量数据失败", e);
        }
    }

    /**
     * 向量搜索
     * @return
     */
    public List<JSONObject> search(String queryText, int topK, int segment) {
        try {
            List<Float> queryVector = getVector(queryText);
            String expr = String.format("segment == %d", segment);

            // 构建查询数据
            SearchReq searchReq = SearchReq.builder()
                    .filter(expr)
                    .collectionName(COLLECTION_NAME)
                    .data(Collections.singletonList(new FloatVec(queryVector)))
                    .topK(topK)
                    .outputFields(Arrays.asList("id", "description", "segment", "description_vector"))
                    .build();

            // 执行搜索
            SearchResp search = milvusClient.search(searchReq);

            List<JSONObject> ret = search.getSearchResults().get(0)
                    .stream().map(item -> JSONObject.from(item)).collect(Collectors.toList());

            return ret;
            
        } catch (Exception e) {
            log.error("向量搜索时发生错误", e);
            throw new RuntimeException("向量搜索失败", e);
        }
    }

    /**
     * 根据ID查询向量数据
     */
    public JSONObject findById(String id) {
        try {
            String expr = String.format("id == %d", Long.parseLong(id));

            QueryReq queryReq = QueryReq.builder()
                    .collectionName(COLLECTION_NAME)
                    .filter(expr)
                    .outputFields(Arrays.asList("id", "description", "segment", "description_vector"))
                    .build();

            QueryResp queryResp = milvusClient.query(queryReq);

            return queryResp.getQueryResults().size() > 0 ? JSONObject.from(queryResp.getQueryResults().get(0)) : null;

        } catch (Exception e) {
            log.error("查询向量数据时发生错误", e);
            throw new RuntimeException("查询向量数据失败", e);
        }
    }
}
