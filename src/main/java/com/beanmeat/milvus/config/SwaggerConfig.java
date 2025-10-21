package com.beanmeat.milvus.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger配置类
 */
@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Beanmeat Milvus API")
                        .description("SpringBoot+Milvus向量数据库+Ollama+Bge-M3向量模型 API文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Beanmeat Team")
                                .email("contact@beanmeat.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}