package com.liyy.demo.milvus;

import io.milvus.v2.client.ConnectConfig;
import io.milvus.v2.client.MilvusClientV2;
import io.milvus.v2.service.vector.request.SearchReq;
import io.milvus.v2.service.vector.request.data.FloatVec;
import io.milvus.v2.service.vector.response.SearchResp;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.beanmeat.milvus.service.VectorDataService.getVector;


/**
 * 向量搜索
 *
 * @author QT-PC-0028
 */
public class SearchData {

    public static final String DB_URI = "http://192.168.1.106:19530";
    public static ConnectConfig config = ConnectConfig.builder().uri(DB_URI).build();
    public static MilvusClientV2 client = new MilvusClientV2(config);

    public static void main(String[] args) {
        // 执行搜索示例
        searchRiskData();
    }

    /**
     * 搜索risk表数据
     */
    private static void searchRiskData() {
        try {
            // 构造查询向量
            String queryText = "绿化养护水管破裂，水流喷洒至电箱";
            List<Float> queryVector = getVector(queryText);

            // 执行搜索
            SearchResp searchResp = client.search(SearchReq.builder()
                    .collectionName("risk")
                    .data(Collections.singletonList(new FloatVec(queryVector)))
                    // 返回最相似的5条记录
                    .topK(5)
                    // 指定返回字段
                    .outputFields(Arrays.asList("business_no", "dept_id", "desc"))
                    .build());

            // 处理搜索结果
            if (searchResp.getSearchResults() != null && !searchResp.getSearchResults().isEmpty()) {
                List<SearchResp.SearchResult> results = searchResp.getSearchResults().get(0);
                System.out.println("搜索 \"" + queryText + "\" 的结果：");
                for (int i = 0; i < results.size(); i++) {
                    SearchResp.SearchResult result = results.get(i);
                    System.out.println("第" + (i + 1) + "条结果，相似度：" + result.getScore());
                    Map<String, Object> entity = result.getEntity();
                    System.out.println("  business_no: " + entity.get("business_no"));
                    System.out.println("  dept_id: " + entity.get("dept_id"));
                    System.out.println("  desc: " + entity.get("desc"));
                    System.out.println();
                }
            } else {
                System.out.println("未找到相关结果");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
