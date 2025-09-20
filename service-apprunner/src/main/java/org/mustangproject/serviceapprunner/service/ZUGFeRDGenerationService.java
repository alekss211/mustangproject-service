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

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Service
public class ZUGFeRDGenerationService {

    public byte[] generateZUGFeRDPDF(InvoiceDTO invoiceData) throws Exception {
        // Create an invoice using the Mustang library
        Invoice invoice = createInvoice(invoiceData);
        
        // Create ZUGFeRD exporter
        try (ZUGFeRDExporterFromA3 exporter = new ZUGFeRDExporterFromA3()) {
            // Set the transaction
            exporter.setTransaction(invoice);
            
            // Export to PDF with embedded ZUGFeRD XML
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            exporter.export(outputStream);
            
            return outputStream.toByteArray();
        }
    }
    
    private Invoice createInvoice(InvoiceDTO dto) {
        Invoice invoice = new Invoice();
        
        // Set basic invoice information
        invoice.setNumber(dto.getInvoiceNumber());
        invoice.setIssueDate(localDateToDate(parseDate(dto.getInvoiceDate())));
        invoice.setDeliveryDate(localDateToDate(parseDate(dto.getIssueDate())));
        invoice.setDueDate(localDateToDate(parseDate(dto.getDueDate())));
        
        // Set seller information (company)
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
        TradeParty recipient = new TradeParty();
        recipient.setName(dto.getBillToInfo1());
        recipient.setStreet(dto.getBillToInfo2());
        recipient.setZIP(dto.getBillToInfo3());
        recipient.setLocation(dto.getBillToInfo3());
        recipient.setCountry(dto.getBillToInfo3());
        invoice.setRecipient(recipient);
        
        // Set currency
        invoice.setCurrency(dto.getCurrencyCode());
        
        // Add invoice items
        if (dto.getItems() != null) {
            for (InvoiceItemDTO itemDto : dto.getItems()) {
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
                }
                
                invoice.addItem(item);
            }
        }
        
        // Set payment terms
        if (dto.getTerms() != null && !dto.getTerms().isEmpty()) {
            invoice.setPaymentTermDescription(dto.getTerms());
        }
        
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
