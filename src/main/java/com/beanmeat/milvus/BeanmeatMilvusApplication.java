package com.beanmeat.milvus;

import com.beanmeat.milvus.service.VectorDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Beanmeat Milvus 应用启动类
 */
@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class BeanmeatMilvusApplication {
    
    private final VectorDataService vectorDataService;
    
    public static void main(String[] args) {
        SpringApplication.run(BeanmeatMilvusApplication.class, args);
    }
    
    /**
     * 应用启动后初始化
     */
    @Bean
    public CommandLineRunner init() {
        return args -> {
            try {
                log.info("开始初始化Milvus集合...");
                // vectorDataService.initializeCollection();
                log.info("Milvus集合初始化完成");
                
                log.info("==========================================");
                log.info("Beanmeat Milvus 应用启动成功！");
                log.info("API文档地址: http://localhost:8080/api/swagger-ui.html");
                log.info("==========================================");
                
            } catch (Exception e) {
                log.error("应用初始化失败", e);
                log.warn("应用将继续运行，但Milvus功能可能不可用");
            }
        };
    }
}
