package com.eventra.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@Slf4j
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> home() {
        log.info("Root endpoint accessed");
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to Eventra API");
        response.put("status", "running");
        response.put("version", "1.0.0");
        response.put("endpoints", Map.of(
            "auth", "/api/auth",
            "user", "/api/user",
            "admin", "/api/admin",
            "status", "/status"
        ));
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/home/health")
    public ResponseEntity<Map<String, String>> health() {
        log.info("Health check endpoint accessed");
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Eventra API is running");
        
        return ResponseEntity.ok(response);
    }
} 