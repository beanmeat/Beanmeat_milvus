package com.beanmeat.milvus.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 系统健康检查控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/health")
@Tag(name = "系统健康检查", description = "系统状态和健康检查API")
public class HealthController {
    
    /**
     * 健康检查
     */
    @GetMapping
    @Operation(summary = "健康检查", description = "检查系统运行状态")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> healthInfo = Map.of(
                "status", "UP",
                "timestamp", LocalDateTime.now(),
                "service", "Beanmeat Milvus Service",
                "version", "1.0.0"
        );
        
        return ResponseEntity.ok(healthInfo);
    }
    
    /**
     * 系统信息
     */
    @GetMapping("/info")
    @Operation(summary = "系统信息", description = "获取系统基本信息")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> systemInfo = Map.of(
                "application", "Beanmeat Milvus",
                "description", "SpringBoot+Milvus向量数据库+Ollama+Bge-M3向量模型",
                "version", "1.0.0",
                "javaVersion", System.getProperty("java.version"),
                "osName", System.getProperty("os.name"),
                "osVersion", System.getProperty("os.version"),
                "timestamp", LocalDateTime.now()
        );
        
        return ResponseEntity.ok(systemInfo);
    }
}
