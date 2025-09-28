package org.mustangproject.serviceapprunner.service;

import org.mustangproject.serviceapprunner.dto.InvoiceDTO;
import org.mustangproject.serviceapprunner.dto.InvoiceItemDTO;
import org.mustangproject.serviceapprunner.dto.TaxDTO;
import org.mustangproject.Invoice;
import org.mustangproject.Item;
import org.mustangproject.Product;
import org.mustangproject.TradeParty;
import org.mustangproject.ZUGFeRD.ZUGFeRDExporterFromA3;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
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
        
        try {
            // Create an invoice using the Mustang library
            logger.info("Creating Invoice object from DTO...");
            Invoice invoice = createInvoice(invoiceData);
            logger.info("Invoice object created successfully");
            
            // Create ZUGFeRD exporter
            logger.info("Creating ZUGFeRD exporter...");
            try (ZUGFeRDExporterFromA3 exporter = new ZUGFeRDExporterFromA3()) {
                logger.info("Setting transaction on exporter...");
                exporter.setTransaction(invoice);
                logger.info("Transaction set successfully");
                
                // Export to PDF with embedded ZUGFeRD XML
                logger.info("Starting PDF export...");
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                exporter.export(outputStream);
                
                byte[] pdfBytes = outputStream.toByteArray();
                logger.info("PDF export completed. Generated {} bytes", pdfBytes.length);
                
                if (pdfBytes.length == 0) {
                    logger.error("Generated PDF is empty!");
                    throw new RuntimeException("Generated PDF is empty");
                }
                
                logger.info("=== ZUGFeRD Service: PDF generation successful ===");
                return pdfBytes;
            }
        } catch (Exception e) {
            logger.error("ZUGFeRD service failed during PDF generation", e);
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
        
        logger.info("Setting dates - issue: {}, delivery: {}, due: {}", 
            dto.getInvoiceDate(), dto.getIssueDate(), dto.getDueDate());
        invoice.setIssueDate(localDateToDate(parseDate(dto.getInvoiceDate())));
        invoice.setDeliveryDate(localDateToDate(parseDate(dto.getIssueDate())));
        invoice.setDueDate(localDateToDate(parseDate(dto.getDueDate())));
        
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
    
    private LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
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
                return LocalDate.parse(dateString, formatter);
            } catch (Exception e) {
                // Continue to next format
            }
        }
        
        // If no format works, return current date
        return LocalDate.now();
    }
    
    private Date localDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
