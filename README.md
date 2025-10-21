# Beanmeat Milvus

SpringBoot + Milvus向量数据库 + Ollama + Bge-M3向量模型

## 项目简介

这是一个基于Spring Boot的向量数据库应用，集成了Milvus向量数据库，提供完整的向量数据增删改查功能，并自动生成Swagger API文档。

## 技术栈

- **Spring Boot 3.2.0** - 主框架
- **Milvus 2.3.4** - 向量数据库
- **Swagger/OpenAPI 3** - API文档生成
- **Maven** - 依赖管理
- **Java 17** - 开发语言

## 功能特性

- ✅ 向量数据的增删改查
- ✅ 向量相似度搜索
- ✅ 分页查询
- ✅ 批量操作
- ✅ 自动生成Swagger API文档
- ✅ 全局异常处理
- ✅ 参数验证
- ✅ 健康检查

## 快速开始

### 1. 环境要求

- Java 17+
- Maven 3.6+
- Milvus 2.3+

### 2. 启动Milvus

```bash
# 使用Docker启动Milvus
docker run -d --name milvus-standalone \
  -p 19530:19530 \
  -p 9091:9091 \
  milvusdb/milvus:latest \
  milvus run standalone
```

### 3. 配置应用

修改 `src/main/resources/application.yml` 中的Milvus连接配置：

```yaml
milvus:
  host: localhost
  port: 19530
  database: default
```

### 4. 运行应用

```bash
# 编译项目
mvn clean compile

# 运行应用
mvn spring-boot:run
```

### 5. 访问API文档

启动成功后，访问以下地址查看API文档：

- Swagger UI: http://localhost:8080/api/swagger-ui.html
- API文档: http://localhost:8080/api/api-docs

## API接口

### 向量数据管理

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/api/v1/vectors/init` | 初始化集合 |
| POST | `/api/v1/vectors` | 创建向量数据 |
| POST | `/api/v1/vectors/batch` | 批量创建向量数据 |
| GET | `/api/v1/vectors/{id}` | 获取向量数据 |
| PUT | `/api/v1/vectors` | 更新向量数据 |
| DELETE | `/api/v1/vectors/{id}` | 删除向量数据 |
| POST | `/api/v1/vectors/search` | 向量搜索 |
| GET | `/api/v1/vectors` | 分页查询 |
| GET | `/api/v1/vectors/statistics` | 获取统计信息 |

### 系统监控

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/api/v1/health` | 健康检查 |
| GET | `/api/v1/health/info` | 系统信息 |

## 使用示例

### 1. 创建向量数据

```bash
curl -X POST http://localhost:8080/api/v1/vectors \
  -H "Content-Type: application/json" \
  -d '{
    "text": "这是一个示例文本",
    "metadata": "{\"category\":\"example\"}",
    "vector": [0.1, 0.2, 0.3, 0.4, 0.5]
  }'
```

### 2. 向量搜索

```bash
curl -X POST http://localhost:8080/api/v1/vectors/search \
  -H "Content-Type: application/json" \
  -d '{
    "queryVector": [0.1, 0.2, 0.3, 0.4, 0.5],
    "topK": 10,
    "threshold": 0.7
  }'
```

### 3. 分页查询

```bash
curl -X GET "http://localhost:8080/api/v1/vectors?page=1&size=10"
```

## 项目结构

```
src/main/java/com/beanmeat/milvus/
├── BeanmeatMilvusApplication.java    # 应用启动类
├── config/                           # 配置类
│   ├── MilvusConfig.java            # Milvus配置
│   ├── MilvusProperties.java        # Milvus属性
│   ├── SwaggerConfig.java           # Swagger配置
│   └── WebConfig.java               # Web配置
├── controller/                       # 控制器层
│   ├── VectorDataController.java    # 向量数据控制器
│   └── HealthController.java        # 健康检查控制器
├── dto/                             # 数据传输对象
│   ├── ApiResponse.java             # 通用响应
│   ├── PageResponse.java            # 分页响应
│   ├── VectorDataCreateRequest.java # 创建请求
│   ├── VectorDataUpdateRequest.java # 更新请求
│   ├── VectorDataResponse.java      # 响应对象
│   └── VectorSearchRequest.java     # 搜索请求
├── entity/                          # 实体类
│   └── VectorData.java             # 向量数据实体
├── exception/                       # 异常处理
│   └── GlobalExceptionHandler.java # 全局异常处理器
├── repository/                      # 数据访问层
│   └── MilvusRepository.java       # Milvus数据访问
└── service/                         # 业务逻辑层
    └── VectorDataService.java      # 向量数据服务
```

## 配置说明

### Milvus配置

```yaml
milvus:
  host: localhost          # Milvus服务器地址
  port: 19530              # Milvus服务器端口
  database: default        # 数据库名称
  collection:
    name: beanmeat_vectors # 集合名称
    description: Beanmeat向量数据集合
    dimension: 1024        # 向量维度
    metric-type: COSINE    # 距离度量类型
```

### Swagger配置

```yaml
springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
    operationsSorter: method
    tagsSorter: alpha
```

## 开发说明

1. **向量维度**: 默认配置为1024维，可根据实际需求调整
2. **距离度量**: 使用COSINE相似度，适合文本向量
3. **索引类型**: 使用IVF_FLAT索引，平衡性能和精度
4. **分页查询**: 支持offset和limit参数
5. **异常处理**: 全局异常处理器统一处理错误

## 许可证

MIT License