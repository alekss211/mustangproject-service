package org.mustangproject.serviceapprunner;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class InvoiceController {

    @GetMapping("/health")
    public String health() {
        return "Mustang Invoice Service is running";
    }

    @GetMapping("/info")
    public String info() {
        return "Mustang Invoice Service - E-invoice processing and validation";
    }

    @GetMapping("/test-interface")
    public ResponseEntity<Map<String, Object>> testInterface() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Success - mustangproject-service is available");
        response.put("service", "mustangproject-service");
        response.put("version", "2.19.1-SNAPSHOT");
        
        return ResponseEntity.ok(response);
    }
}
