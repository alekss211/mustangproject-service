package org.mustangproject.serviceapprunner.service;

import org.mustangproject.serviceapprunner.dto.InvoiceDTO;
import org.mustangproject.serviceapprunner.dto.InvoiceItemDTO;
import org.mustangproject.Invoice;
import org.mustangproject.Item;
import org.mustangproject.Product;
import org.mustangproject.SchemedID;
import org.mustangproject.TradeParty;
import org.mustangproject.Contact;
import org.mustangproject.BankDetails;
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
            
            byte[] pdfBytes;
            CustomZUGFeRDVisualizer visualizer = new CustomZUGFeRDVisualizer();
            pdfBytes = visualizer.toPDF(xmlContent);
            logger.info("PDF creation with CustomZUGFeRDVisualizer (minimal layout) completed in {}ms. Generated {} bytes", 
                System.currentTimeMillis() - stepStart, pdfBytes.length);
            
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
        
        // Debug DTO fields for payment information
        logger.info("=== DTO PAYMENT FIELDS DEBUG ===");
        logger.info("iban field: '{}'", dto.getIban());
        logger.info("bic field: '{}'", dto.getBic());
        logger.info("company_info5: '{}'", dto.getCompanyInfo5());
        logger.info("company_info6: '{}'", dto.getCompanyInfo6());
        logger.info("company_info7: '{}'", dto.getCompanyInfo7());
        logger.info("company_info8: '{}'", dto.getCompanyInfo8());
        logger.info("company_info9: '{}'", dto.getCompanyInfo9());
        logger.info("terms: '{}'", dto.getTerms());
        logger.info("=== END DTO PAYMENT FIELDS DEBUG ===");
        
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
        // Note: We don't have separate address fields in the DTO, so we'll leave ZIP/Location/Country empty for now
        // to avoid conflicts with email and website fields
        
        // Set seller URI communication (website) if available
        if (dto.getCompanyInfo2() != null && !dto.getCompanyInfo2().isEmpty()) {
            logger.info("Setting seller website: {}", dto.getCompanyInfo2());
            SchemedID websiteId = new SchemedID("URI", dto.getCompanyInfo2());
            sender.addUriUniversalCommunicationID(websiteId);
        }

        // Set seller contact information if available (email)
        if (dto.getCompanyInfo3() != null && !dto.getCompanyInfo3().isEmpty()) {
            logger.info("Setting seller email: {}", dto.getCompanyInfo3());
            Contact sellerContact = new Contact();
            sellerContact.setEMail(dto.getCompanyInfo3());
            sender.setContact(sellerContact);
        }

        // Set seller VAT ID if available (this represents the Ust-Id)
        if (dto.getCompanyInfo4() != null && !dto.getCompanyInfo4().isEmpty()) {
            logger.info("Setting seller VAT ID: {}", dto.getCompanyInfo4());
            sender.setVATID(dto.getCompanyInfo4());
        }

        // Set seller bank details if available
        if (dto.getIban() != null && !dto.getIban().isEmpty()) {
            logger.info("Setting seller bank details: IBAN={}, BIC={}", dto.getIban(), dto.getBic());
            BankDetails bankDetails = new BankDetails(dto.getIban(), dto.getBic());
            bankDetails.setAccountName(dto.getCompanyName());
            sender.addBankDetails(bankDetails);
        }

        invoice.setSender(sender);
        
        // Set buyer information
        logger.info("Setting buyer information: name={}, street={}, email={}",
            dto.getBillToInfo1(), dto.getBillToInfo2(), dto.getBillToInfo4());
        TradeParty recipient = new TradeParty();
        recipient.setName(dto.getBillToInfo1());
        recipient.setStreet(dto.getBillToInfo2());
        recipient.setZIP(dto.getBillToInfo3());
        recipient.setLocation(dto.getBillToInfo3());
        recipient.setCountry(dto.getBillToInfo3());

        // Set recipient contact information if available (email)
        if (dto.getBillToInfo4() != null && !dto.getBillToInfo4().isEmpty()) {
            logger.info("Setting recipient email: {}", dto.getBillToInfo4());
            Contact recipientContact = new Contact();
            recipientContact.setEMail(dto.getBillToInfo4());
            recipient.setContact(recipientContact);
        }

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
                // Use BigDecimal.valueOf() to avoid precision issues with double conversion
                item.setQuantity(BigDecimal.valueOf(itemDto.getItemQuantity()));
                item.setPrice(BigDecimal.valueOf(itemDto.getItemPrice()));
                
                // Set VAT percentage from item data
                // The VAT percentage is provided by the frontend/backend
                BigDecimal vatPercent = BigDecimal.valueOf(itemDto.getItemTax());
                product.setVATPercent(vatPercent);
                logger.info("Setting VAT percentage for item '{}': {}%", itemDto.getItemDescription(), vatPercent);
                
                invoice.addItem(item);
            }
        }
        
        // Set payment terms with bank details appended
        String paymentTerms = "";
        if (dto.getTerms() != null && !dto.getTerms().isEmpty()) {
            paymentTerms = dto.getTerms();
        }
        
        // Extract IBAN and BIC from either dedicated fields or company_info fields
        String iban = dto.getIban();
        String bic = dto.getBic();
        
        logger.info("Initial IBAN from dedicated field: {}", iban);
        logger.info("Initial BIC from dedicated field: {}", bic);
        logger.info("company_info7 value: {}", dto.getCompanyInfo7());
        logger.info("company_info8 value: {}", dto.getCompanyInfo8());
        
        // If IBAN/BIC are not in dedicated fields, use company_info7/8 directly
        if ((iban == null || iban.isEmpty()) && dto.getCompanyInfo7() != null && !dto.getCompanyInfo7().isEmpty()) {
            String info7 = dto.getCompanyInfo7().trim();
            logger.info("Using company_info7 as IBAN: '{}'", info7);
            // Check if it contains the word "iban" (formatted case), otherwise use as-is
            if (info7.toLowerCase().contains("iban")) {
                iban = info7.replaceAll("(?i)iban\\s*:?\\s*", "").trim();
                logger.info("Extracted IBAN from formatted company_info7: '{}'", iban);
            } else {
                iban = info7;
                logger.info("Using company_info7 directly as IBAN: '{}'", iban);
            }
        } else {
            logger.info("Skipping IBAN extraction - iban field: '{}', company_info7: '{}'", iban, dto.getCompanyInfo7());
        }
        
        if ((bic == null || bic.isEmpty()) && dto.getCompanyInfo8() != null && !dto.getCompanyInfo8().isEmpty()) {
            String info8 = dto.getCompanyInfo8().trim();
            logger.info("Using company_info8 as BIC: '{}'", info8);
            // Check if it contains the word "bic" (formatted case), otherwise use as-is
            if (info8.toLowerCase().contains("bic")) {
                bic = info8.replaceAll("(?i)bic\\s*:?\\s*", "").trim();
                logger.info("Extracted BIC from formatted company_info8: '{}'", bic);
            } else {
                bic = info8;
                logger.info("Using company_info8 directly as BIC: '{}'", bic);
            }
        } else {
            logger.info("Skipping BIC extraction - bic field: '{}', company_info8: '{}'", bic, dto.getCompanyInfo8());
        }
        
        // Append bank details if available
        if (iban != null && !iban.isEmpty()) {
            if (!paymentTerms.isEmpty()) {
                paymentTerms += "\n\n";
            }
            paymentTerms += "Zahlungsinformationen:\n";
            paymentTerms += "Empfänger: " + (dto.getCompanyName() != null ? dto.getCompanyName() : "N/A") + "\n";
            paymentTerms += "IBAN: " + iban + "\n";
            if (bic != null && !bic.isEmpty()) {
                paymentTerms += "BIC: " + bic;
            }
            logger.info("Appended bank details to payment terms. Final IBAN: '{}', BIC: '{}'", iban, bic);
        } else {
            logger.warn("NOT appending bank details - IBAN is null or empty: '{}'", iban);
        }
        
        if (!paymentTerms.isEmpty()) {
            logger.info("Setting payment terms: '{}'", paymentTerms);
            invoice.setPaymentTermDescription(paymentTerms);
        } else {
            logger.warn("Payment terms are empty - not setting payment term description");
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

    /**
     * Generate XML debug output for testing
     */
    public String generateXMLDebug(InvoiceDTO dto) {
        logger.info("=== XML DEBUG: Starting XML generation ===");
        
        try {
            // Create invoice object
            Invoice invoice = createInvoice(dto);
            logger.info("XML DEBUG: Invoice object created successfully");
            
            // Generate XML using Mustang library
            ZUGFeRD2PullProvider provider = new ZUGFeRD2PullProvider();
            provider.generateXML(invoice);
            byte[] xmlBytes = provider.getXML();
            String xmlContent = new String(xmlBytes, "UTF-8");
            logger.info("XML DEBUG: XML export completed, length: {} characters", xmlContent.length());
            
            return xmlContent;
            
        } catch (Exception e) {
            logger.error("XML DEBUG: Failed to generate XML", e);
            throw new RuntimeException("Failed to generate XML debug: " + e.getMessage(), e);
        }
    }
}
