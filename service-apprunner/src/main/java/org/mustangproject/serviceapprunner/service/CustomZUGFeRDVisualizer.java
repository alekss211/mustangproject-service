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
    }

    /**
     * Override the PDF template loading to use our custom template
     */
    @Override
    protected void applyXSLTToPDF(java.io.InputStream xmlFile, 
                                  java.io.OutputStream PDFOutstream)
        throws javax.xml.transform.TransformerException, java.io.IOException {
        
        logger.info("Using custom PDF template for XSLT transformation");
        
        try {
            // Load our custom PDF template instead of the default one
            if (mXsltCustomPDFTemplate == null) {
                logger.info("Loading custom PDF template: stylesheets/custom-invoice-layout-standalone.xsl");
                InputStream templateStream = CLASS_LOADER.getResourceAsStream("stylesheets/custom-invoice-layout-standalone.xsl");
                if (templateStream == null) {
                    logger.warn("Custom template not found, falling back to default template");
                    super.applyXSLTToPDF(xmlFile, PDFOutstream);
                    return;
                }
                
                // Use reflection to access the private mFactory field
                try {
                    java.lang.reflect.Field factoryField = ZUGFeRDVisualizer.class.getDeclaredField("mFactory");
                    factoryField.setAccessible(true);
                    javax.xml.transform.TransformerFactory factory = (javax.xml.transform.TransformerFactory) factoryField.get(this);
                    
                    mXsltCustomPDFTemplate = factory.newTemplates(new StreamSource(templateStream));
                    logger.info("Custom PDF template loaded successfully");
                } catch (Exception e) {
                    logger.error("Failed to access TransformerFactory", e);
                    super.applyXSLTToPDF(xmlFile, PDFOutstream);
                    return;
                }
            }
            
            // Apply our custom template
            javax.xml.transform.Transformer transformer = mXsltCustomPDFTemplate.newTransformer();
            transformer.transform(new StreamSource(xmlFile), new StreamResult(PDFOutstream));
            logger.info("Custom PDF transformation completed successfully");
            
        } catch (TransformerConfigurationException e) {
            logger.error("Failed to load custom PDF template, falling back to default", e);
            super.applyXSLTToPDF(xmlFile, PDFOutstream);
        }
    }

    /**
     * Generate PDF from XML content using our custom template
     */
    public byte[] toPDF(String xmlContent) {
        logger.info("=== CustomZUGFeRDVisualizer: Generating PDF with custom layout ===");
        logger.info("Input XML content length: {} characters", xmlContent != null ? xmlContent.length() : 0);
        
        try {
            // Use the parent class's toPDF method which will call our custom applyXSLTToPDF
            return super.toPDF(xmlContent);
            
        } catch (Exception e) {
            logger.error("Failed to generate PDF with custom layout", e);
            throw new RuntimeException("Failed to generate PDF with custom layout", e);
        }
    }
}