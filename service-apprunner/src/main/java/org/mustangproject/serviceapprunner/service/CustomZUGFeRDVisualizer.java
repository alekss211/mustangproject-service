package org.mustangproject.serviceapprunner.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.fop.apps.*;
import org.apache.fop.apps.io.ResourceResolverFactory;
import org.apache.fop.configuration.Configuration;
import org.apache.fop.configuration.DefaultConfigurationBuilder;
import org.apache.xmlgraphics.util.MimeConstants;
import org.mustangproject.ClasspathResolverURIAdapter;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.XMLConstants;

import java.io.*;
import java.util.Map;

public class CustomZUGFeRDVisualizer {

    private static final Logger logger = LoggerFactory.getLogger(CustomZUGFeRDVisualizer.class);
    
    private static final ClassLoader CLASS_LOADER = CustomZUGFeRDVisualizer.class.getClassLoader();
    
    private FopFactory fopFactory;
    private FOUserAgent userAgent;

    public CustomZUGFeRDVisualizer() {
        try {
            // Initialize FOP with configuration
            DefaultConfigurationBuilder cfgBuilder = new DefaultConfigurationBuilder();
            Configuration cfg = cfgBuilder.build(CLASS_LOADER.getResourceAsStream("fop-config.xconf"));
            
            FopFactoryBuilder builder = new FopFactoryBuilder(new File(".").toURI(), new ClasspathResolverURIAdapter())
                .setConfiguration(cfg);
            this.fopFactory = builder.build();
            
            this.fopFactory.getFontManager().setResourceResolver(
                ResourceResolverFactory.createInternalResourceResolver(
                    new File(".").toURI(),
                    new ClasspathResolverURIAdapter()));
            
            this.userAgent = fopFactory.newFOUserAgent();
            @SuppressWarnings("unchecked")
            Map<String, Object> rendererOptions = (Map<String, Object>) this.userAgent.getRendererOptions();
            rendererOptions.put("pdf-a-mode", "PDF/A-3b");
            
        } catch (Exception e) {
            logger.error("Failed to initialize FOP", e);
        }
    }

    /**
     * Generate PDF from XML content using our custom template
     */
    public byte[] toPDF(String xmlContent) {
        logger.info("Generating PDF with custom layout from XML content");
        
        try {
            // Create a simple invoice XML structure for our custom template
            String customXml = createCustomInvoiceXML();
            
            // Transform to XSL-FO using our custom template
            String foContent = transformToFO(customXml);
            
            // Generate PDF from FO
            return generatePDFFromFO(foContent);
            
        } catch (Exception e) {
            logger.error("Failed to generate PDF with custom layout", e);
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    /**
     * Create a custom XML structure for our invoice template
     */
    private String createCustomInvoiceXML() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
               "<invoice>" +
               "  <company>" +
               "    <name>Musterfirma</name>" +
               "    <address>Musterstraße 123, 12345 Musterstadt</address>" +
               "    <website>www.musterfirma.de</website>" +
               "    <email>kontakt@musterfirma.de</email>" +
               "    <vatId>Ust.-ID: DE1234567890</vatId>" +
               "  </company>" +
               "  <payment>" +
               "    <accountHolder>Kontoinhaber: Musterfirma</accountHolder>" +
               "    <iban>IBAN: DE12345678912345678912</iban>" +
               "    <bic>BIC: ABCDEFGH</bic>" +
               "  </payment>" +
               "  <recipient>" +
               "    <name>Max Mustermann</name>" +
               "    <address>Musterstraße 123</address>" +
               "    <city>12345 Musterstadt, Deutschland</city>" +
               "  </recipient>" +
               "  <invoice>" +
               "    <number>1</number>" +
               "    <date>08.09.2025</date>" +
               "    <serviceDate>08.09.2025</serviceDate>" +
               "    <dueDate>08.10.2025</dueDate>" +
               "  </invoice>" +
               "  <items>" +
               "    <item>" +
               "      <position>1</position>" +
               "      <description>Musterleistung</description>" +
               "      <quantity>1</quantity>" +
               "      <price>100,00 €</price>" +
               "      <discount>0%</discount>" +
               "      <tax>19%</tax>" +
               "      <total>100,00 €</total>" +
               "    </item>" +
               "  </items>" +
               "  <totals>" +
               "    <netAmount>100,00 €</netAmount>" +
               "    <tax>19,00 €</tax>" +
               "    <totalAmount>119,00 €</totalAmount>" +
               "  </totals>" +
               "  <notes>Vielen Dank für Ihren Auftrag. Bitte senden Sie Zahlungen vor dem Fälligkeitsdatum.</notes>" +
               "</invoice>";
    }

    /**
     * Transform XML to XSL-FO using our custom template
     */
    private String transformToFO(String xmlContent) throws Exception {
        logger.info("Transforming XML to XSL-FO using custom template");
        
        TransformerFactory factory = TransformerFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        
        // Load our custom XSLT template
        StreamSource xsltSource = new StreamSource(
            CLASS_LOADER.getResourceAsStream("stylesheets/custom-invoice-layout.xsl")
        );
        
        Transformer transformer = factory.newTransformer(xsltSource);
        
        // Transform the XML
        Source xmlSource = new StreamSource(new StringReader(xmlContent));
        StringWriter resultWriter = new StringWriter();
        Result result = new StreamResult(resultWriter);
        
        transformer.transform(xmlSource, result);
        
        String foContent = resultWriter.toString();
        logger.info("XSL-FO transformation completed. FO content length: {} characters", foContent.length());
        
        return foContent;
    }

    /**
     * Generate PDF from XSL-FO content
     */
    private byte[] generatePDFFromFO(String foContent) throws Exception {
        logger.info("Generating PDF from XSL-FO content");
        
        ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
        
        try {
            // Create FOP instance for PDF output
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, userAgent, pdfOutputStream);
            
            // Setup transformer
            TransformerFactory factory = TransformerFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            Transformer transformer = factory.newTransformer();
            
            // Transform FO to PDF
            Source foSource = new StreamSource(new StringReader(foContent));
            Result pdfResult = new SAXResult(fop.getDefaultHandler());
            
            transformer.transform(foSource, pdfResult);
            
        } catch (Exception e) {
            logger.error("Failed to generate PDF from FO", e);
            throw e;
        }
        
        byte[] pdfBytes = pdfOutputStream.toByteArray();
        logger.info("PDF generation completed. Generated {} bytes", pdfBytes.length);
        
        return pdfBytes;
    }
}
