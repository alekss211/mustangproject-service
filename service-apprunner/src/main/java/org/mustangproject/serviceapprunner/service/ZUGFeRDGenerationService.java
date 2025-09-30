package org.mustangproject.serviceapprunner.service;

import org.mustangproject.serviceapprunner.dto.InvoiceDTO;
import org.mustangproject.serviceapprunner.dto.InvoiceItemDTO;
import org.mustangproject.serviceapprunner.dto.TaxDTO;
import org.mustangproject.Invoice;
import org.mustangproject.Item;
import org.mustangproject.Product;
import org.mustangproject.TradeParty;
import org.mustangproject.ZUGFeRD.ZUGFeRD2PullProvider;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Service
public class ZUGFeRDGenerationService {

    private static final Logger logger = LoggerFactory.getLogger(ZUGFeRDGenerationService.class);

    public byte[] generateZUGFeRDPDF(InvoiceDTO invoiceData) throws Exception {
        logger.info("=== ZUGFeRD Service: Starting PDF generation ===");
        long startTime = System.currentTimeMillis();
        
        try {
            // Create an invoice using the Mustang library
            logger.info("Creating Invoice object from DTO...");
            long stepStart = System.currentTimeMillis();
            Invoice invoice = createInvoice(invoiceData);
            logger.info("Invoice object created successfully in {}ms", System.currentTimeMillis() - stepStart);
            
            // Generate XML from invoice
            logger.info("Generating XML from invoice...");
            stepStart = System.currentTimeMillis();
            String xmlContent = generateXMLFromInvoice(invoice);
            logger.info("XML generation completed in {}ms. XML length: {} characters", 
                System.currentTimeMillis() - stepStart, xmlContent.length());
            
            // Create PDF from XML using CustomZUGFeRDVisualizer
            logger.info("Creating PDF from XML using CustomZUGFeRDVisualizer...");
            stepStart = System.currentTimeMillis();
            
            byte[] pdfBytes = null;
            try {
                CustomZUGFeRDVisualizer visualizer = new CustomZUGFeRDVisualizer();
                pdfBytes = visualizer.toPDF(xmlContent);
                logger.info("PDF creation with CustomZUGFeRDVisualizer completed in {}ms. Generated {} bytes", 
                    System.currentTimeMillis() - stepStart, pdfBytes.length);
            } catch (Exception customVisualizerException) {
                logger.error("CustomZUGFeRDVisualizer failed, trying default ZUGFeRDVisualizer", customVisualizerException);
                
                // Fallback to default ZUGFeRDVisualizer
                try {
                    org.mustangproject.ZUGFeRD.ZUGFeRDVisualizer defaultVisualizer = new org.mustangproject.ZUGFeRD.ZUGFeRDVisualizer();
                    pdfBytes = defaultVisualizer.toPDF(xmlContent);
                    logger.info("PDF creation with default ZUGFeRDVisualizer completed in {}ms. Generated {} bytes", 
                        System.currentTimeMillis() - stepStart, pdfBytes.length);
                } catch (Exception defaultVisualizerException) {
                    logger.error("Both custom and default visualizers failed", defaultVisualizerException);
                    throw new Exception("PDF generation failed with both custom and default visualizers", defaultVisualizerException);
                }
            }
            
            if (pdfBytes.length == 0) {
                logger.error("Generated PDF is empty!");
                throw new RuntimeException("Generated PDF is empty");
            }
            
            long totalTime = System.currentTimeMillis() - startTime;
            logger.info("=== ZUGFeRD Service: PDF generation successful in {}ms ===", totalTime);
            return pdfBytes;
            
        } catch (Exception e) {
            long totalTime = System.currentTimeMillis() - startTime;
            logger.error("ZUGFeRD service failed during PDF generation after {}ms", totalTime, e);
            throw e;
        }
    }
    
    private Invoice createInvoice(InvoiceDTO dto) {
        logger.info("Creating Invoice object with data: number={}, company={}", 
            dto.getInvoiceNumber(), dto.getCompanyName());
        
        Invoice invoice = new Invoice();
        
        // Set basic invoice information
        logger.info("Setting invoice number: {}", dto.getInvoiceNumber());
        invoice.setNumber(dto.getInvoiceNumber());
        
        logger.info("Setting dates - invoice: {}, issue: {}, due: {}", 
            dto.getInvoiceDate(), dto.getIssueDate(), dto.getDueDate());
        
        LocalDate invoiceDate = parseDate(dto.getInvoiceDate());
        LocalDate issueDate = parseDate(dto.getIssueDate());
        LocalDate dueDate = parseDate(dto.getDueDate());
        
        logger.info("Parsed dates - invoice: {}, issue: {}, due: {}", 
            invoiceDate, issueDate, dueDate);
        
        invoice.setIssueDate(localDateToDate(invoiceDate));
        invoice.setDeliveryDate(localDateToDate(issueDate));
        invoice.setDueDate(localDateToDate(dueDate));
        
        // Set seller information (company)
        logger.info("Setting seller information: name={}, street={}", 
            dto.getCompanyName(), dto.getCompanyInfo1());
        TradeParty sender = new TradeParty();
        sender.setName(dto.getCompanyName());
        sender.setStreet(dto.getCompanyInfo1());
        sender.setZIP(dto.getCompanyInfo3());
        sender.setLocation(dto.getCompanyInfo3());
        sender.setCountry(dto.getCompanyInfo3());
        sender.setVATID(dto.getCompanyInfo4());
        // Note: Tax number is typically part of VAT ID in ZUGFeRD
        invoice.setSender(sender);
        
        // Set buyer information
        logger.info("Setting buyer information: name={}, street={}", 
            dto.getBillToInfo1(), dto.getBillToInfo2());
        TradeParty recipient = new TradeParty();
        recipient.setName(dto.getBillToInfo1());
        recipient.setStreet(dto.getBillToInfo2());
        recipient.setZIP(dto.getBillToInfo3());
        recipient.setLocation(dto.getBillToInfo3());
        recipient.setCountry(dto.getBillToInfo3());
        invoice.setRecipient(recipient);
        
        // Set currency
        logger.info("Setting currency: {}", dto.getCurrencyCode());
        invoice.setCurrency(dto.getCurrencyCode());
        
        // Add invoice items
        logger.info("Adding {} invoice items", dto.getItems() != null ? dto.getItems().size() : 0);
        if (dto.getItems() != null) {
            for (int i = 0; i < dto.getItems().size(); i++) {
                InvoiceItemDTO itemDto = dto.getItems().get(i);
                logger.info("Processing item {}: description={}, quantity={}, price={}", 
                    i + 1, itemDto.getItemDescription(), itemDto.getItemQuantity(), itemDto.getItemPrice());
                
                Item item = new Item();
                Product product = new Product();
                product.setName(itemDto.getItemDescription());
                item.setProduct(product);
                item.setQuantity(new BigDecimal(itemDto.getItemQuantity()));
                item.setPrice(new BigDecimal(itemDto.getItemPrice()));
                // Note: Unit and VAT are typically set on the product level
                if (dto.getTaxes() != null && !dto.getTaxes().isEmpty()) {
                    TaxDTO tax = dto.getTaxes().get(0); // Use first tax
                    product.setVATPercent(new BigDecimal(tax.getTaxPercentage()));
                    logger.info("Set VAT percentage: {}%", tax.getTaxPercentage());
                }
                
                invoice.addItem(item);
            }
        }
        
        // Set payment terms
        if (dto.getTerms() != null && !dto.getTerms().isEmpty()) {
            logger.info("Setting payment terms: {}", dto.getTerms());
            invoice.setPaymentTermDescription(dto.getTerms());
        }
        
        logger.info("Invoice object creation completed successfully");
        return invoice;
    }
    
    private String generateXMLFromInvoice(Invoice invoice) throws Exception {
        logger.info("Converting Invoice to XML using ZUGFeRD2PullProvider...");
        
        if (invoice == null) {
            logger.error("Invoice object is null - cannot generate XML");
            throw new IllegalArgumentException("Invoice cannot be null");
        }
        
        try {
            // Use the Mustang library's ZUGFeRD2PullProvider to generate proper XML
            logger.info("Creating ZUGFeRD2PullProvider...");
            ZUGFeRD2PullProvider xmlProvider = new ZUGFeRD2PullProvider();
            logger.info("ZUGFeRD2PullProvider created successfully");
            
            logger.info("Calling xmlProvider.generateXML(invoice)...");
            xmlProvider.generateXML(invoice);
            logger.info("XML generation from invoice completed successfully");
            
            byte[] xmlBytes = xmlProvider.getXML();
            if (xmlBytes == null) {
                logger.error("ZUGFeRD2PullProvider returned null XML bytes");
                throw new RuntimeException("XML generation failed - null result from provider");
            }
            
            if (xmlBytes.length == 0) {
                logger.error("ZUGFeRD2PullProvider returned empty XML bytes");
                throw new RuntimeException("XML generation failed - empty result from provider");
            }
            
            String xmlContent = new String(xmlBytes, java.nio.charset.StandardCharsets.UTF_8);
            
            logger.info("XML generation completed. Generated XML with {} characters", xmlContent.length());
            
            // Debug XML content thoroughly
            if (xmlContent.isEmpty()) {
                logger.error("XML content is EMPTY!");
                throw new RuntimeException("Generated XML is empty");
            }
            
            // Check for BOM or invisible characters at the start
            char firstChar = xmlContent.charAt(0);
            int firstCharCode = (int) firstChar;
            logger.info("First character of XML: '{}' (ASCII: {})", firstChar, firstCharCode);
            
            // Log first 500 characters with escape sequences visible
            String xmlPreview = xmlContent.length() > 500 ? xmlContent.substring(0, 500) + "..." : xmlContent;
            logger.info("Generated XML content preview (first 500 chars): {}", xmlPreview);
            
            // Check if XML starts properly
            if (!xmlContent.trim().startsWith("<?xml")) {
                logger.error("XML does not start with proper XML declaration!");
                logger.error("XML starts with: '{}'", xmlContent.substring(0, Math.min(100, xmlContent.length())));
                
                // Try to find where actual XML starts
                int xmlStart = xmlContent.indexOf("<?xml");
                if (xmlStart > 0) {
                    logger.warn("Found XML declaration at position {}. Trimming invalid content before it.", xmlStart);
                    xmlContent = xmlContent.substring(xmlStart);
                    logger.info("Trimmed XML now starts with: '{}'", xmlContent.substring(0, Math.min(100, xmlContent.length())));
                }
            }
            
            // Clean up the XML content to ensure it's valid
            xmlContent = cleanXMLContent(xmlContent);
            
            return xmlContent;
            
        } catch (Exception e) {
            logger.error("Failed to generate XML from invoice - Exception type: {}, Message: {}", 
                e.getClass().getSimpleName(), e.getMessage(), e);
            throw new Exception("XML generation failed: " + e.getMessage(), e);
        }
    }
    
    private LocalDate parseDate(String dateString) {
        logger.debug("Parsing date string: '{}'", dateString);
        
        if (dateString == null || dateString.isEmpty()) {
            logger.warn("Date string is null or empty, using current date");
            return LocalDate.now();
        }
        
        // Try different date formats
        DateTimeFormatter[] formatters = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd.MM.yyyy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy")
        };
        
        for (DateTimeFormatter formatter : formatters) {
            try {
                LocalDate result = LocalDate.parse(dateString, formatter);
                logger.debug("Successfully parsed '{}' using format '{}' -> {}", 
                    dateString, formatter.toString(), result);
                return result;
            } catch (Exception e) {
                logger.debug("Failed to parse '{}' with format '{}': {}", 
                    dateString, formatter.toString(), e.getMessage());
            }
        }
        
        // If no format works, return current date
        logger.warn("Could not parse date '{}' with any format, using current date", dateString);
        return LocalDate.now();
    }
    
    private Date localDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
    
    /**
     * Clean XML content to remove BOM, invalid characters, and ensure proper format
     */
    private String cleanXMLContent(String xmlContent) {
        if (xmlContent == null || xmlContent.isEmpty()) {
            return xmlContent;
        }
        
        logger.debug("Cleaning XML content...");
        
        // Remove BOM (Byte Order Mark) if present
        if (xmlContent.startsWith("\uFEFF")) {
            logger.warn("Removing BOM from XML content");
            xmlContent = xmlContent.substring(1);
        }
        
        // Remove any leading whitespace or control characters
        String originalStart = xmlContent.substring(0, Math.min(50, xmlContent.length()));
        xmlContent = xmlContent.trim();
        
        if (!originalStart.equals(xmlContent.substring(0, Math.min(50, xmlContent.length())))) {
            logger.info("Trimmed leading whitespace from XML content");
        }
        
        // Ensure XML starts with proper declaration
        if (!xmlContent.startsWith("<?xml")) {
            logger.error("XML content does not start with XML declaration after cleaning");
            // Log the first 200 characters in hex format for debugging
            StringBuilder hexDump = new StringBuilder();
            for (int i = 0; i < Math.min(200, xmlContent.length()); i++) {
                char c = xmlContent.charAt(i);
                hexDump.append(String.format("%02X ", (int) c));
                if ((i + 1) % 16 == 0) hexDump.append("\n");
            }
            logger.error("XML content hex dump (first 200 chars):\n{}", hexDump.toString());
        }
        
        return xmlContent;
    }
}
