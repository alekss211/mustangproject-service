package org.mustangproject.serviceapprunner.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mustangproject.ZUGFeRD.ZUGFeRDVisualizer;

import javax.xml.transform.Templates;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import java.io.InputStream;

public class CustomZUGFeRDVisualizer extends ZUGFeRDVisualizer {

    private static final Logger logger = LoggerFactory.getLogger(CustomZUGFeRDVisualizer.class);
    private static final ClassLoader CLASS_LOADER = CustomZUGFeRDVisualizer.class.getClassLoader();
    
    private Templates mXsltCustomPDFTemplate = null;

    public CustomZUGFeRDVisualizer() {
        super(); // Initialize parent ZUGFeRDVisualizer
        logger.info("CustomZUGFeRDVisualizer initialized - using minimal layout");
    }

    /**
     * Use the minimal layout template for clean, simple invoice generation
     */
    @Override
    protected String toFOP(java.io.InputStream is, org.mustangproject.EStandard theStandard)
        throws javax.xml.transform.TransformerException, java.io.IOException {
        
        logger.info("Using minimal layout template for FOP generation");
        
        try {
            // Load our minimal PDF template instead of the default one
            if (mXsltCustomPDFTemplate == null) {
                logger.info("Loading minimal PDF template: stylesheets/layout-minimal.xsl");
                InputStream templateStream = CLASS_LOADER.getResourceAsStream("stylesheets/layout-minimal.xsl");
                if (templateStream == null) {
                    logger.warn("Minimal template not found, falling back to default template");
                    return super.toFOP(is, theStandard);
                }
                
                // Use reflection to access the private mFactory field
                try {
                    java.lang.reflect.Field factoryField = ZUGFeRDVisualizer.class.getDeclaredField("mFactory");
                    factoryField.setAccessible(true);
                    javax.xml.transform.TransformerFactory factory = (javax.xml.transform.TransformerFactory) factoryField.get(this);
                    
                    if (factory == null) {
                        logger.error("TransformerFactory is null - cannot create template");
                        logger.info("Falling back to default parent template...");
                        return super.toFOP(is, theStandard);
                    }
                    
                    logger.debug("Creating templates from minimal XSL stream...");
                    mXsltCustomPDFTemplate = factory.newTemplates(new StreamSource(templateStream));
                    logger.info("Minimal PDF template loaded successfully");
                    
                    // Close the stream
                    templateStream.close();
                    
                } catch (Exception e) {
                    logger.error("Failed to access TransformerFactory or create template", e);
                    logger.info("Falling back to default parent template...");
                    try {
                        if (templateStream != null) templateStream.close();
                    } catch (Exception closeEx) {
                        logger.warn("Failed to close template stream", closeEx);
                    }
                    return super.toFOP(is, theStandard);
                }
            }
            
            // Apply our minimal template to generate FOP
            logger.info("Creating ByteArrayOutputStream for FOP output...");
            java.io.ByteArrayOutputStream fopOutput = new java.io.ByteArrayOutputStream();
            
            logger.info("Creating transformer from minimal template...");
            javax.xml.transform.Transformer transformer = mXsltCustomPDFTemplate.newTransformer();
            
            logger.info("Applying XSLT transformation to generate FOP...");
            transformer.transform(new StreamSource(is), new StreamResult(fopOutput));
            logger.info("XSLT transformation completed successfully");
            
            String fopResult = fopOutput.toString("UTF-8");
            logger.info("Minimal FOP generation completed successfully. Generated {} characters", fopResult.length());
            
            return fopResult;
            
        } catch (Exception e) {
            logger.error("Failed to generate FOP with minimal template, falling back to default", e);
            return super.toFOP(is, theStandard);
        }
    }

    /**
     * Generate PDF from XML content using the minimal layout
     */
    public byte[] toPDF(String xmlContent) {
        logger.info("=== CustomZUGFeRDVisualizer: Generating PDF with minimal layout ===");
        logger.info("Input XML content length: {} characters", xmlContent != null ? xmlContent.length() : 0);
        
        if (xmlContent == null || xmlContent.trim().isEmpty()) {
            logger.error("XML content is null or empty - cannot generate PDF");
            throw new IllegalArgumentException("XML content cannot be null or empty");
        }
        
        // Debug XML content thoroughly
        char firstChar = xmlContent.charAt(0);
        int firstCharCode = (int) firstChar;
        logger.info("CustomZUGFeRDVisualizer - First character of XML: '{}' (ASCII: {})", firstChar, firstCharCode);
        
        // Check if XML starts properly
        if (!xmlContent.trim().startsWith("<?xml")) {
            logger.error("CustomZUGFeRDVisualizer - XML does not start with proper XML declaration!");
            logger.error("XML starts with: '{}'", xmlContent.substring(0, Math.min(100, xmlContent.length())));
        }
        
        // Log first 500 characters of XML for debugging
        String xmlPreview = xmlContent.length() > 500 ? xmlContent.substring(0, 500) + "..." : xmlContent;
        logger.info("CustomZUGFeRDVisualizer - XML content preview: {}", xmlPreview);
        
        try {
            logger.info("Calling parent toPDF method...");
            byte[] result = super.toPDF(xmlContent);
            
            if (result == null) {
                logger.error("Parent toPDF returned null result");
                throw new RuntimeException("PDF generation returned null result");
            }
            
            if (result.length == 0) {
                logger.error("Parent toPDF returned empty result");
                throw new RuntimeException("PDF generation returned empty result");
            }
            
            logger.info("PDF generation successful - generated {} bytes", result.length);
            return result;
            
        } catch (Exception e) {
            logger.error("Failed to generate PDF with custom layout - Exception type: {}, Message: {}", 
                e.getClass().getSimpleName(), e.getMessage(), e);
            
            // Log the full stack trace for debugging
            logger.error("Full stack trace:", e);
            
            throw new RuntimeException("Failed to generate PDF with minimal layout: " + e.getMessage(), e);
        }
    }
}