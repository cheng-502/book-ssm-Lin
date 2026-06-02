package com.bookssm.controller;

import com.bookssm.dto.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/api/health")
    public Result<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("message", "SSM project is running");
        data.put("timestamp", System.currentTimeMillis());
        return Result.success(data);
    }

    @GetMapping("/api/health/db")
    public Result<Map<String, Object>> db() {
        String version = jdbcTemplate.queryForObject("SELECT VERSION()", String.class);
        Map<String, Object> data = new HashMap<>();
        data.put("db", "MySQL");
        data.put("version", version);
        return Result.success("数据库连接正常", data);
    }
}
