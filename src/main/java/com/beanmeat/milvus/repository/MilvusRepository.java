package com.beanmeat.milvus.repository;

import com.beanmeat.milvus.entity.VectorData;
import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.DataType;
import io.milvus.grpc.SearchResults;
import io.milvus.param.R;
import io.milvus.param.collection.*;
import io.milvus.param.dml.*;
import io.milvus.param.index.*;
import io.milvus.response.SearchResultsWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Milvus数据访问层
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class MilvusRepository {
    
    private final MilvusServiceClient milvusClient;
    private final String collectionName = "beanmeat_vectors";
    
    /**
     * 创建集合
     */
    public void createCollection() {
        try {
            // 检查集合是否存在
            R<Boolean> hasCollectionR = milvusClient.hasCollection(
                    HasCollectionParam.newBuilder()
                            .withCollectionName(collectionName)
                            .build()
            );
            
            if (hasCollectionR.getData()) {
                log.info("集合 {} 已存在", collectionName);
                return;
            }
            
            // 定义字段
            List<FieldType> fields = Arrays.asList(
                    FieldType.newBuilder()
                            .withName("id")
                            .withDataType(DataType.VarChar)
                            .withMaxLength(100)
                            .withPrimaryKey(true)
                            .build(),
                    FieldType.newBuilder()
                            .withName("vector")
                            .withDataType(DataType.FloatVector)
                            .withDimension(1024)
                            .build(),
                    FieldType.newBuilder()
                            .withName("text")
                            .withDataType(DataType.VarChar)
                            .withMaxLength(1000)
                            .build(),
                    FieldType.newBuilder()
                            .withName("metadata")
                            .withDataType(DataType.VarChar)
                            .withMaxLength(2000)
                            .build(),
                    FieldType.newBuilder()
                            .withName("create_time")
                            .withDataType(DataType.Int64)
                            .build(),
                    FieldType.newBuilder()
                            .withName("update_time")
                            .withDataType(DataType.Int64)
                            .build()
            );
            
            // 创建集合
            R<RpcStatus> createCollectionR = milvusClient.createCollection(
                    CreateCollectionParam.newBuilder()
                            .withCollectionName(collectionName)
                            .withDescription("Beanmeat向量数据集合")
                            .withShardsNum(2)
                            .withFieldTypes(fields)
                            .build()
            );
            
            if (createCollectionR.getStatus() == R.Status.Success.getCode()) {
                log.info("集合 {} 创建成功", collectionName);
                
                // 创建索引
                createIndex();
                
                // 加载集合
                loadCollection();
            } else {
                log.error("集合 {} 创建失败: {}", collectionName, createCollectionR.getMessage());
                throw new RuntimeException("集合创建失败");
            }
            
        } catch (Exception e) {
            log.error("创建集合时发生错误", e);
            throw new RuntimeException("创建集合失败", e);
        }
    }
    
    /**
     * 创建索引
     */
    private void createIndex() {
        try {
            R<RpcStatus> createIndexR = milvusClient.createIndex(
                    CreateIndexParam.newBuilder()
                            .withCollectionName(collectionName)
                            .withFieldName("vector")
                            .withIndexType(IndexType.IVF_FLAT)
                            .withMetricType(MetricType.COSINE)
                            .withExtraParam("{\"nlist\":1024}")
                            .build()
            );
            
            if (createIndexR.getStatus() == R.Status.Success.getCode()) {
                log.info("向量字段索引创建成功");
            } else {
                log.error("向量字段索引创建失败: {}", createIndexR.getMessage());
            }
        } catch (Exception e) {
            log.error("创建索引时发生错误", e);
        }
    }
    
    /**
     * 加载集合
     */
    private void loadCollection() {
        try {
            R<RpcStatus> loadCollectionR = milvusClient.loadCollection(
                    LoadCollectionParam.newBuilder()
                            .withCollectionName(collectionName)
                            .build()
            );
            
            if (loadCollectionR.getStatus() == R.Status.Success.getCode()) {
                log.info("集合 {} 加载成功", collectionName);
            } else {
                log.error("集合 {} 加载失败: {}", collectionName, loadCollectionR.getMessage());
            }
        } catch (Exception e) {
            log.error("加载集合时发生错误", e);
        }
    }
    
    /**
     * 插入向量数据
     */
    public void insert(VectorData vectorData) {
        try {
            List<String> ids = Arrays.asList(vectorData.getId());
            List<List<Float>> vectors = Arrays.asList(vectorData.getVector());
            List<String> texts = Arrays.asList(vectorData.getText());
            List<String> metadata = Arrays.asList(vectorData.getMetadata());
            List<Long> createTimes = Arrays.asList(System.currentTimeMillis());
            List<Long> updateTimes = Arrays.asList(System.currentTimeMillis());
            
            List<InsertParam.Field> fields = Arrays.asList(
                    new InsertParam.Field("id", ids),
                    new InsertParam.Field("vector", vectors),
                    new InsertParam.Field("text", texts),
                    new InsertParam.Field("metadata", metadata),
                    new InsertParam.Field("create_time", createTimes),
                    new InsertParam.Field("update_time", updateTimes)
            );
            
            R<MutationResult> insertR = milvusClient.insert(
                    InsertParam.newBuilder()
                            .withCollectionName(collectionName)
                            .withFields(fields)
                            .build()
            );
            
            if (insertR.getStatus() == R.Status.Success.getCode()) {
                log.info("向量数据插入成功, ID: {}", vectorData.getId());
            } else {
                log.error("向量数据插入失败: {}", insertR.getMessage());
                throw new RuntimeException("向量数据插入失败");
            }
            
        } catch (Exception e) {
            log.error("插入向量数据时发生错误", e);
            throw new RuntimeException("插入向量数据失败", e);
        }
    }
    
    /**
     * 批量插入向量数据
     */
    public void insertBatch(List<VectorData> vectorDataList) {
        try {
            List<String> ids = vectorDataList.stream()
                    .map(VectorData::getId)
                    .collect(Collectors.toList());
            
            List<List<Float>> vectors = vectorDataList.stream()
                    .map(VectorData::getVector)
                    .collect(Collectors.toList());
            
            List<String> texts = vectorDataList.stream()
                    .map(VectorData::getText)
                    .collect(Collectors.toList());
            
            List<String> metadata = vectorDataList.stream()
                    .map(VectorData::getMetadata)
                    .collect(Collectors.toList());
            
            long currentTime = System.currentTimeMillis();
            List<Long> createTimes = vectorDataList.stream()
                    .map(v -> currentTime)
                    .collect(Collectors.toList());
            
            List<Long> updateTimes = vectorDataList.stream()
                    .map(v -> currentTime)
                    .collect(Collectors.toList());
            
            List<InsertParam.Field> fields = Arrays.asList(
                    new InsertParam.Field("id", ids),
                    new InsertParam.Field("vector", vectors),
                    new InsertParam.Field("text", texts),
                    new InsertParam.Field("metadata", metadata),
                    new InsertParam.Field("create_time", createTimes),
                    new InsertParam.Field("update_time", updateTimes)
            );
            
            R<MutationResult> insertR = milvusClient.insert(
                    InsertParam.newBuilder()
                            .withCollectionName(collectionName)
                            .withFields(fields)
                            .build()
            );
            
            if (insertR.getStatus() == R.Status.Success.getCode()) {
                log.info("批量插入向量数据成功, 数量: {}", vectorDataList.size());
            } else {
                log.error("批量插入向量数据失败: {}", insertR.getMessage());
                throw new RuntimeException("批量插入向量数据失败");
            }
            
        } catch (Exception e) {
            log.error("批量插入向量数据时发生错误", e);
            throw new RuntimeException("批量插入向量数据失败", e);
        }
    }
    
    /**
     * 根据ID删除向量数据
     */
    public void deleteById(String id) {
        try {
            String expr = String.format("id == \"%s\"", id);
            
            R<MutationResult> deleteR = milvusClient.delete(
                    DeleteParam.newBuilder()
                            .withCollectionName(collectionName)
                            .withExpr(expr)
                            .build()
            );
            
            if (deleteR.getStatus() == R.Status.Success.getCode()) {
                log.info("向量数据删除成功, ID: {}", id);
            } else {
                log.error("向量数据删除失败: {}", deleteR.getMessage());
                throw new RuntimeException("向量数据删除失败");
            }
            
        } catch (Exception e) {
            log.error("删除向量数据时发生错误", e);
            throw new RuntimeException("删除向量数据失败", e);
        }
    }
    
    /**
     * 根据ID查询向量数据
     */
    public VectorData findById(String id) {
        try {
            String expr = String.format("id == \"%s\"", id);
            
            List<String> outputFields = Arrays.asList("id", "vector", "text", "metadata", "create_time", "update_time");
            
            R<QueryResults> queryR = milvusClient.query(
                    QueryParam.newBuilder()
                            .withCollectionName(collectionName)
                            .withExpr(expr)
                            .withOutFields(outputFields)
                            .build()
            );
            
            if (queryR.getStatus() == R.Status.Success.getCode()) {
                QueryResults queryResults = queryR.getData();
                if (queryResults.getRowRecords().size() > 0) {
                    return convertToVectorData(queryResults.getRowRecords().get(0));
                }
            } else {
                log.error("查询向量数据失败: {}", queryR.getMessage());
            }
            
            return null;
            
        } catch (Exception e) {
            log.error("查询向量数据时发生错误", e);
            throw new RuntimeException("查询向量数据失败", e);
        }
    }
    
    /**
     * 向量相似度搜索
     */
    public List<VectorData> search(List<Float> queryVector, int topK, double threshold, String filter) {
        try {
            List<String> outputFields = Arrays.asList("id", "vector", "text", "metadata", "create_time", "update_time");
            
            SearchParam.Builder searchBuilder = SearchParam.newBuilder()
                    .withCollectionName(collectionName)
                    .withVectorFieldName("vector")
                    .withVectors(Arrays.asList(queryVector))
                    .withTopK(topK)
                    .withOutFields(outputFields)
                    .withParams("{\"nprobe\":10}");
            
            if (filter != null && !filter.trim().isEmpty()) {
                searchBuilder.withExpr(filter);
            }
            
            R<SearchResults> searchR = milvusClient.search(searchBuilder.build());
            
            if (searchR.getStatus() == R.Status.Success.getCode()) {
                SearchResults searchResults = searchR.getData();
                SearchResultsWrapper wrapper = new SearchResultsWrapper(searchResults.getResults());
                
                List<VectorData> results = new ArrayList<>();
                
                for (int i = 0; i < wrapper.getIDScore(0).size(); i++) {
                    String id = wrapper.getIDScore(0).get(i).getStrID();
                    Float score = wrapper.getIDScore(0).get(i).getScore();
                    
                    // 过滤相似度阈值
                    if (score >= threshold) {
                        VectorData vectorData = findById(id);
                        if (vectorData != null) {
                            vectorData.setScore(score);
                            results.add(vectorData);
                        }
                    }
                }
                
                return results;
            } else {
                log.error("向量搜索失败: {}", searchR.getMessage());
                throw new RuntimeException("向量搜索失败");
            }
            
        } catch (Exception e) {
            log.error("向量搜索时发生错误", e);
            throw new RuntimeException("向量搜索失败", e);
        }
    }
    
    /**
     * 分页查询向量数据
     */
    public List<VectorData> findAll(int offset, int limit) {
        try {
            List<String> outputFields = Arrays.asList("id", "vector", "text", "metadata", "create_time", "update_time");
            
            R<QueryResults> queryR = milvusClient.query(
                    QueryParam.newBuilder()
                            .withCollectionName(collectionName)
                            .withOutFields(outputFields)
                            .withOffset(offset)
                            .withLimit(limit)
                            .build()
            );
            
            if (queryR.getStatus() == R.Status.Success.getCode()) {
                QueryResults queryResults = queryR.getData();
                return queryResults.getRowRecords().stream()
                        .map(this::convertToVectorData)
                        .collect(Collectors.toList());
            } else {
                log.error("分页查询向量数据失败: {}", queryR.getMessage());
                throw new RuntimeException("分页查询向量数据失败");
            }
            
        } catch (Exception e) {
            log.error("分页查询向量数据时发生错误", e);
            throw new RuntimeException("分页查询向量数据失败", e);
        }
    }
    
    /**
     * 获取总数量
     */
    public long count() {
        try {
            R<GetCollectionStatisticsResponse> statsR = milvusClient.getCollectionStatistics(
                    GetCollectionStatisticsParam.newBuilder()
                            .withCollectionName(collectionName)
                            .build()
            );
            
            if (statsR.getStatus() == R.Status.Success.getCode()) {
                return statsR.getData().getRowNum();
            } else {
                log.error("获取集合统计信息失败: {}", statsR.getMessage());
                return 0;
            }
            
        } catch (Exception e) {
            log.error("获取集合统计信息时发生错误", e);
            return 0;
        }
    }
    
    /**
     * 将查询结果转换为VectorData对象
     */
    private VectorData convertToVectorData(QueryResults.RowRecord rowRecord) {
        VectorData vectorData = new VectorData();
        vectorData.setId(rowRecord.get("id").getStringData().getData(0));
        vectorData.setText(rowRecord.get("text").getStringData().getData(0));
        vectorData.setMetadata(rowRecord.get("metadata").getStringData().getData(0));
        
        // 转换向量数据
        List<Float> vector = new ArrayList<>();
        for (Float value : rowRecord.get("vector").getFloatVector().getDataList()) {
            vector.add(value);
        }
        vectorData.setVector(vector);
        
        // 转换时间戳
        if (rowRecord.get("create_time") != null) {
            long createTime = rowRecord.get("create_time").getLongData().getData(0);
            vectorData.setCreateTime(new java.sql.Timestamp(createTime).toLocalDateTime());
        }
        
        if (rowRecord.get("update_time") != null) {
            long updateTime = rowRecord.get("update_time").getLongData().getData(0);
            vectorData.setUpdateTime(new java.sql.Timestamp(updateTime).toLocalDateTime());
        }
        
        return vectorData;
    }
}
