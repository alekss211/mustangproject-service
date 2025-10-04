package org.mustangproject.serviceapprunner;

import org.mustangproject.serviceapprunner.dto.InvoiceDTO;
import org.mustangproject.serviceapprunner.dto.InvoiceItemDTO;
import org.mustangproject.serviceapprunner.dto.TaxDTO;
import org.mustangproject.serviceapprunner.service.ZUGFeRDGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private ZUGFeRDGenerationService zugferdGenerationService;

    @PostMapping(value = "/generate-zugferd-local", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> testZUGFeRDGeneration() {
        logger.info("=== LOCAL TEST: ZUGFeRD Generation Started ===");
        
        try {
            // Create test data based on your actual backend data
            InvoiceDTO testInvoice = createTestInvoiceData();
            
            logger.info("Test invoice created: {}", testInvoice.getInvoiceNumber());
            
            // Generate ZUGFeRD PDF
            byte[] pdfBytes = zugferdGenerationService.generateZUGFeRDPDF(testInvoice);
            
            logger.info("PDF generated successfully: {} bytes", pdfBytes.length);
            
            // Return PDF
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "test_invoice_" + testInvoice.getInvoiceNumber() + "_zugferd.pdf");
            headers.setContentLength(pdfBytes.length);
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            logger.error("LOCAL TEST: ZUGFeRD generation failed", e);
            
            // Return detailed error response
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to generate ZUGFeRD PDF: " + e.getMessage());
            errorResponse.put("service", "mustangproject-service-local-test");
            errorResponse.put("error_type", e.getClass().getSimpleName());
            errorResponse.put("stack_trace", e.getStackTrace()[0].toString());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
        } finally {
            logger.info("=== LOCAL TEST: ZUGFeRD Generation Completed ===");
        }
    }

    @PostMapping(value = "/generate-xml-debug", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> testXMLDebug() {
        logger.info("=== LOCAL TEST: XML Debug Started ===");
        
        try {
            // Create test data based on your actual backend data
            InvoiceDTO testInvoice = createTestInvoiceData();
            
            logger.info("Test invoice created: {}", testInvoice.getInvoiceNumber());
            
            // Generate XML for debugging
            String xmlContent = zugferdGenerationService.generateXMLDebug(testInvoice);
            
            logger.info("XML generated successfully: {} characters", xmlContent.length());
            
            // Return XML
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);
            headers.setContentDispositionFormData("attachment", "debug_invoice_" + testInvoice.getInvoiceNumber() + ".xml");
            
            return new ResponseEntity<>(xmlContent, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            logger.error("LOCAL TEST: XML debug generation failed", e);
            
            // Return detailed error response
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to generate XML debug: " + e.getMessage());
            errorResponse.put("service", "mustangproject-service-local-test");
            errorResponse.put("error_type", e.getClass().getSimpleName());
            errorResponse.put("stack_trace", e.getStackTrace()[0].toString());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
        } finally {
            logger.info("=== LOCAL TEST: XML Debug Completed ===");
        }
    }
    
    private InvoiceDTO createTestInvoiceData() {
        InvoiceDTO invoice = new InvoiceDTO();
        
        // Company information
        invoice.setCompanyName("RechnungsFlow");
        invoice.setCompanyInfo1("Musterstraße 123, 12345 Musterstadt");
        invoice.setCompanyInfo2("www.musterfirma.de");
        invoice.setCompanyInfo3("kontakt@musterfirma.de");
        invoice.setCompanyInfo4("Ust.-ID: DE1234567890");
        
        // Bill to information (recipient)
        invoice.setBillToInfo1("Max Mustermann");
        invoice.setBillToInfo2("Musterstraße 123");
        invoice.setBillToInfo3("12345 Musterstadt, Deutschland");
        
        // Invoice details
        invoice.setInvoiceNumber("113123123");
        invoice.setInvoiceDate("2025-09-08");
        invoice.setIssueDate("2025-09-08");
        invoice.setDueDate("2025-10-08");
        
        // Amounts
        invoice.setAmountSubtotal(100.0);
        invoice.setAmountTotal(119.0);
        invoice.setAmountDue(119.0);
        
        // Terms
        invoice.setTerms("Vielen Dank für Ihren Auftrag. Bitte senden Sie Zahlungen vor dem Fälligkeitsdatum.");
        
        // Items
        List<InvoiceItemDTO> items = new ArrayList<>();
        InvoiceItemDTO item = new InvoiceItemDTO();
        item.setItemDescription("Musterleistung");
        item.setItemQuantity(1.0);
        item.setItemPrice(100.0);
        item.setItemDiscount(0.0);
        item.setItemTax(19.0);
        item.setItemLineTotal(100.0);
        items.add(item);
        invoice.setItems(items);
        
        // Taxes
        List<TaxDTO> taxes = new ArrayList<>();
        TaxDTO tax = new TaxDTO();
        tax.setTaxName("Steuer 19%:");
        tax.setTaxValue(19.0);
        tax.setTaxPercentage(19.0);
        taxes.add(tax);
        invoice.setTaxes(taxes);
        
        return invoice;
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> testHealth() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "ok");
        response.put("message", "Local test service is running");
        response.put("timestamp", java.time.Instant.now().toString());
        return ResponseEntity.ok(response);
    }
}
