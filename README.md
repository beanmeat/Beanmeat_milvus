# Beanmeat Milvus

SpringBoot + Milvus向量数据库 + Ollama + Bge-M3向量模型

## 项目简介

这是一个基于Spring Boot的向量数据库应用，集成了Milvus向量数据库，提供完整的向量数据增删改查功能，并自动生成Swagger API文档。

## 技术栈

- **Spring Boot 3.2.0** - 主框架
- **Milvus 2.5.2** - 向量数据库（V2客户端）
- **Swagger/OpenAPI 3** - API文档生成
- **Maven** - 依赖管理
- **Java 17** - 开发语言
- **FastJSON2** - JSON处理

## 功能特性

- ✅ 向量数据的增删改查
- ✅ 向量相似度搜索
- ✅ 分页查询
- ✅ 批量操作
- ✅ 自动生成Swagger API文档
- ✅ 全局异常处理
- ✅ 参数验证
- ✅ 健康检查
- ✅ 配置化管理

## 数据模型

根据CreateManualCollection的字段结构，数据模型包含：

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | Int64 | 主键ID |
| description | VarChar(1024) | 描述文本 |
| segment | Int64 | 段落ID |
| description_vector | FloatVector(1024) | 描述向量 |

## 快速开始

### 1. 环境要求

- Java 17+
- Maven 3.6+
- Milvus 2.5+

### 2. 配置Milvus连接

修改 `src/main/resources/application.yml` 中的Milvus连接配置：

```yaml
milvus:
  uri: http://134.175.83.172  # 您的Milvus服务器地址
  collection:
    name: beanmeat_test
    description: Beanmeat向量数据集合
    consistency-level: STRONG
    fields:
      id:
        data-type: Int64
        is-primary-key: true
        max-length: 64
      description:
        data-type: VarChar
        max-length: 1024
      segment:
        data-type: Int64
        max-length: 64
      description_vector:
        data-type: FloatVector
        dimension: 1024
        index-type: FLAT
        metric-type: COSINE
```

### 3. 运行应用

```bash
# 编译项目
mvn clean compile

# 运行应用
mvn spring-boot:run
```

### 4. 访问API文档

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
    "description": "这是一个示例描述文本",
    "segment": 1,
    "descriptionVector": [0.1, 0.2, 0.3, 0.4, 0.5]
  }'
```

### 2. 向量搜索

```bash
curl -X POST http://localhost:8080/api/v1/vectors/search \
  -H "Content-Type: application/json" \
  -d '{
    "queryVector": [0.1, 0.2, 0.3, 0.4, 0.5],
    "topK": 10,
    "threshold": 0.7,
    "filter": "segment == 1"
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
│   ├── MilvusConfig.java            # Milvus V2配置
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
│   ├── MilvusRepository.java       # Milvus V2数据访问
│   └── CreateManualCollection.java  # 手动创建集合示例
└── service/                         # 业务逻辑层
    └── VectorDataService.java      # 向量数据服务
```

## 配置说明

### Milvus配置

```yaml
milvus:
  uri: http://134.175.83.172          # Milvus服务器URI
  collection:
    name: beanmeat_test               # 集合名称
    description: Beanmeat向量数据集合
    consistency-level: STRONG         # 一致性级别
    fields:                          # 字段配置
      id:                            # ID字段
        data-type: Int64
        is-primary-key: true
        max-length: 64
      description:                   # 描述字段
        data-type: VarChar
        max-length: 1024
      segment:                       # 段落字段
        data-type: Int64
        max-length: 64
      description_vector:            # 向量字段
        data-type: FloatVector
        dimension: 1024
        index-type: FLAT
        metric-type: COSINE
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
3. **索引类型**: 使用FLAT索引，适合小规模数据
4. **主键类型**: 使用Int64类型，支持时间戳生成
5. **分页查询**: 支持offset和limit参数
6. **异常处理**: 全局异常处理器统一处理错误
7. **配置管理**: 所有常量都提取到配置文件中

## 许可证

MIT License