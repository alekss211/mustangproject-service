package org.mustangproject.serviceapprunner;

import org.mustangproject.serviceapprunner.dto.InvoiceDTO;
import org.mustangproject.serviceapprunner.service.ZUGFeRDGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class InvoiceController {

    @Autowired
    private ZUGFeRDGenerationService zugferdGenerationService;

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

    @PostMapping(value = "/generate-zugferd", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generateZUGFeRD(@RequestBody InvoiceDTO invoiceData) {
        try {
            // Generate ZUGFeRD PDF
            byte[] pdfBytes = zugferdGenerationService.generateZUGFeRDPDF(invoiceData);
            
            // Set response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", 
                "invoice_" + invoiceData.getInvoiceNumber() + "_zugferd.pdf");
            headers.setContentLength(pdfBytes.length);
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            // Return error response
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to generate ZUGFeRD PDF: " + e.getMessage());
            errorResponse.put("service", "mustangproject-service");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(convertMapToJsonBytes(errorResponse));
        }
    }
    
    private byte[] convertMapToJsonBytes(Map<String, Object> map) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper()
                .writeValueAsBytes(map);
        } catch (Exception e) {
            return "{\"error\": \"Failed to serialize error response\"}".getBytes();
        }
    }
}
