package com.smart.ecommerce.controller;

import com.smart.ecommerce.dto.ApiResponse;
import com.smart.ecommerce.util.MessageUtil;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health")
@RequiredArgsConstructor
public class HealthController {
    private final MessageUtil messageUtil;

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, String>>> health() {
        return ResponseEntity.ok(ApiResponse.success(
                messageUtil.getMessage("health.success"),
                Map.of("status", messageUtil.getMessage("health.status"))
        ));
    }
}
