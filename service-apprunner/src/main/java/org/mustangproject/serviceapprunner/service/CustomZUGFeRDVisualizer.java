package org.mustangproject.serviceapprunner.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mustangproject.ZUGFeRD.ZUGFeRDVisualizer;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import java.io.InputStream;

public class CustomZUGFeRDVisualizer extends ZUGFeRDVisualizer {

    private static final Logger logger = LoggerFactory.getLogger(CustomZUGFeRDVisualizer.class);
    private static final ClassLoader CLASS_LOADER = CustomZUGFeRDVisualizer.class.getClassLoader();
    
    private Templates mXsltCustomPDFTemplate = null;

    public CustomZUGFeRDVisualizer() {
        super(); // Initialize parent ZUGFeRDVisualizer
        logger.info("CustomZUGFeRDVisualizer initialized with custom PDF template");
        
        // Test if custom template is accessible during initialization
        InputStream testStream = CLASS_LOADER.getResourceAsStream("stylesheets/custom-invoice-layout-standalone.xsl");
        if (testStream != null) {
            logger.info("Custom XSL template found and accessible during initialization");
            try {
                testStream.close();
            } catch (Exception e) {
                logger.warn("Failed to close test stream", e);
            }
        } else {
            logger.warn("Custom XSL template NOT found during initialization - will fall back to default");
        }
    }

    /**
     * Override the toFOP method to use our custom template for FOP generation
     */
    @Override
    protected String toFOP(java.io.InputStream is, org.mustangproject.EStandard theStandard)
        throws javax.xml.transform.TransformerException, java.io.IOException {
        
        logger.info("Using custom template for FOP generation");
        
        try {
            // Load our custom PDF template instead of the default one
            if (mXsltCustomPDFTemplate == null) {
                logger.info("Loading custom PDF template: stylesheets/custom-invoice-layout-standalone.xsl");
                InputStream templateStream = CLASS_LOADER.getResourceAsStream("stylesheets/custom-invoice-layout-standalone.xsl");
                if (templateStream == null) {
                    logger.warn("Custom template not found, falling back to default template");
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
                    
                    logger.debug("Creating templates from custom XSL stream...");
                    mXsltCustomPDFTemplate = factory.newTemplates(new StreamSource(templateStream));
                    logger.info("Custom PDF template loaded successfully");
                    
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
            
            // Apply our custom template to generate FOP
            java.io.ByteArrayOutputStream fopOutput = new java.io.ByteArrayOutputStream();
            javax.xml.transform.Transformer transformer = mXsltCustomPDFTemplate.newTransformer();
            transformer.transform(new StreamSource(is), new StreamResult(fopOutput));
            
            String fopResult = fopOutput.toString("UTF-8");
            logger.info("Custom FOP generation completed successfully. Generated {} characters", fopResult.length());
            
            return fopResult;
            
        } catch (Exception e) {
            logger.error("Failed to generate FOP with custom template, falling back to default", e);
            return super.toFOP(is, theStandard);
        }
    }

    /**
     * Generate PDF from XML content using our custom template
     */
    public byte[] toPDF(String xmlContent) {
        logger.info("=== CustomZUGFeRDVisualizer: Generating PDF with custom layout ===");
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
            
            throw new RuntimeException("Failed to generate PDF with custom layout: " + e.getMessage(), e);
        }
    }
}