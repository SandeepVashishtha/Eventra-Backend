package com.eventra.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class SimpleHealthController {

    @GetMapping("/api")
    public ResponseEntity<Map<String, Object>> apiRoot() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Eventra Backend API is running");
        response.put("version", "1.0.0");
        response.put("endpoints", Map.of(
            "auth", "/api/auth",
            "health", "/health"
        ));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/health")
    public ResponseEntity<Map<String, String>> apiHealth() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Eventra Backend");
        return ResponseEntity.ok(response);
    }
}
