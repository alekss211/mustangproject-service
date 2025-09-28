package org.mustangproject.serviceapprunner;

import org.mustangproject.serviceapprunner.dto.InvoiceDTO;
import org.mustangproject.serviceapprunner.service.ZUGFeRDGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class InvoiceController {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceController.class);

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
    public ResponseEntity<?> generateZUGFeRD(@RequestBody(required = false) InvoiceDTO invoiceData) {
        logger.info("=== ZUGFeRD Generation Request Started ===");
        
        // Check for invalid JSON (null request body)
        if (invoiceData == null) {
            logger.error("Error: Invalid JSON - Request body is null");
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Error: Invalid JSON");
            errorResponse.put("service", "mustangproject-service");
            return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
        }
        
        try {
            logger.info("Received invoice data for invoice number: {}", 
                invoiceData.getInvoiceNumber() != null ? invoiceData.getInvoiceNumber() : "null");
            logger.info("Company name: {}", 
                invoiceData.getCompanyName() != null ? invoiceData.getCompanyName() : "null");
            logger.info("Number of items: {}", 
                invoiceData.getItems() != null ? invoiceData.getItems().size() : 0);
            logger.info("Total amount: {}", 
                invoiceData.getAmountTotal() != null ? invoiceData.getAmountTotal() : "null");
            
            // Generate ZUGFeRD PDF
            logger.info("Starting ZUGFeRD PDF generation...");
            byte[] pdfBytes = zugferdGenerationService.generateZUGFeRDPDF(invoiceData);
            logger.info("ZUGFeRD PDF generation completed successfully. PDF size: {} bytes", pdfBytes.length);
            
            if (pdfBytes.length == 0) {
                logger.error("Generated PDF is empty!");
                throw new RuntimeException("Generated PDF is empty");
            }
            
            // Set response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", 
                "invoice_" + invoiceData.getInvoiceNumber() + "_zugferd.pdf");
            headers.setContentLength(pdfBytes.length);
            
            logger.info("Returning PDF with {} bytes to client", pdfBytes.length);
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            logger.error("ZUGFeRD generation failed", e);
            
            // Return detailed error response
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to generate ZUGFeRD PDF: " + e.getMessage());
            errorResponse.put("service", "mustangproject-service");
            errorResponse.put("error_type", e.getClass().getSimpleName());
            errorResponse.put("stack_trace", e.getStackTrace()[0].toString());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
        } finally {
            logger.info("=== ZUGFeRD Generation Request Completed ===");
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
