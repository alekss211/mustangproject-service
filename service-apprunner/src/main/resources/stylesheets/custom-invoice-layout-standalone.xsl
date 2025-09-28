<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:fo="http://www.w3.org/1999/XSL/Format" 
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:axf="http://www.antennahouse.com/names/XSL/Extensions"
                xmlns:xr="urn:ce.eu:en16931:2017:xoev-de:kosit:standard:xrechnung-1"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:xrv="http://www.example.org/XRechnung-Viewer"          
                version="2.0">

  <xsl:output method="xml" version="1.0" encoding="utf-8" /> 

  <!-- Required parameters -->
  <xsl:param name="foengine"/>
  <xsl:param name="invoiceline-layout">normal</xsl:param>
  <xsl:param name="invoiceline-numbering">normal</xsl:param>
  <xsl:param name="tabular-layout-widths">2 7 2 2 2 2 1.3 2</xsl:param>
  <xsl:param name="axf.extensions" select="false()"/>
  <xsl:param name="fop.extensions" select="true()"/>

  <!-- Font and styling variables -->
  <xsl:variable name="fontSans">Arial</xsl:variable>
  <xsl:variable name="fontSerif">Times</xsl:variable>
  <xsl:variable name="lang">de</xsl:variable>

  <!-- Main invoice template -->
  <xsl:template match="xr:invoice">
    <fo:root xmlns:pdf="http://xmlgraphics.apache.org/fop/extensions/pdf"
      language="{$lang}" font-family="{$fontSans}">
      
      <!-- Custom layout master set -->
      <fo:layout-master-set>
        <fo:simple-page-master master-name="CustomInvoicePage" 
                               page-height="297mm" 
                               page-width="210mm">
          <fo:region-body region-name="xrBody"
                          margin="20mm 15mm 20mm 15mm"/>
          <fo:region-before region-name="header" extent="0mm"/>
          <fo:region-after region-name="footer" extent="0mm"/>
        </fo:simple-page-master>

        <fo:page-sequence-master master-name="xrDokument">
          <fo:repeatable-page-master-alternatives>
            <fo:conditional-page-master-reference master-reference="CustomInvoicePage" />
          </fo:repeatable-page-master-alternatives>
        </fo:page-sequence-master>
      </fo:layout-master-set>

      <!-- PDF metadata -->
      <fo:declarations>
        <x:xmpmeta xmlns:x="adobe:ns:meta/">
          <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
            <rdf:Description rdf:about="" xmlns:dc="http://purl.org/dc/elements/1.1/">
                <dc:title><rdf:Alt><rdf:li xml:lang="x-default"><xsl:value-of select="xr:Invoice_number"/></rdf:li></rdf:Alt></dc:title>
            </rdf:Description>
          </rdf:RDF>
        </x:xmpmeta>
      </fo:declarations>

      <fo:page-sequence master-reference="xrDokument">
        <fo:flow flow-name="xrBody">
          <!-- Custom Header Section -->
          <xsl:call-template name="custom-header-section"/>
          
          <!-- Custom Invoice Details Section -->
          <xsl:call-template name="custom-invoice-details-section"/>
          
          <!-- Custom Invoice Items Table -->
          <xsl:call-template name="custom-invoice-items-table"/>
          
          <!-- Custom Amount Summary -->
          <xsl:call-template name="custom-amount-summary"/>
          
          <!-- Custom Notes Section -->
          <xsl:call-template name="custom-notes-section"/>
        </fo:flow>
      </fo:page-sequence>     
    </fo:root>
  </xsl:template>

  <!-- Custom Header Section: Company Info (left) + Payment Details (right) -->
  <xsl:template name="custom-header-section">
    <fo:block-container display-align="before" height="60mm" margin-bottom="10mm">
      <fo:block-container width="50%" float="left">
        <!-- Company Information from ZUGFeRD data -->
        <fo:block font-family="{$fontSans}" font-size="18pt" font-weight="bold" margin-bottom="5mm">
          <xsl:value-of select="xr:SELLER/xr:Seller_name"/>
        </fo:block>
        <fo:block font-family="{$fontSans}" font-size="10pt" margin-bottom="2mm">
          <xsl:value-of select="xr:SELLER/xr:Seller_postal_address/xr:Seller_street_name"/>
          <xsl:text>, </xsl:text>
          <xsl:value-of select="xr:SELLER/xr:Seller_postal_address/xr:Seller_postal_code"/>
          <xsl:text> </xsl:text>
          <xsl:value-of select="xr:SELLER/xr:Seller_postal_address/xr:Seller_city"/>
        </fo:block>
        <fo:block font-family="{$fontSans}" font-size="10pt" margin-bottom="2mm">
          <xsl:value-of select="xr:SELLER/xr:Seller_contact_information/xr:Seller_phone"/>
        </fo:block>
        <fo:block font-family="{$fontSans}" font-size="10pt" margin-bottom="2mm">
          <xsl:value-of select="xr:SELLER/xr:Seller_contact_information/xr:Seller_email"/>
        </fo:block>
        <fo:block font-family="{$fontSans}" font-size="10pt">
          <xsl:text>Ust.-ID: </xsl:text>
          <xsl:value-of select="xr:SELLER/xr:Seller_VAT_identifier"/>
        </fo:block>
      </fo:block-container>

      <fo:block-container width="45%" float="right">
        <!-- Payment Details -->
        <fo:block font-family="{$fontSans}" font-size="10pt" font-weight="bold" margin-bottom="5mm">
          Zahlungsdetails:
        </fo:block>
        <fo:block font-family="{$fontSans}" font-size="10pt" margin-bottom="2mm">
          Kontoinhaber: <xsl:value-of select="xr:SELLER/xr:Seller_name"/>
        </fo:block>
        <fo:block font-family="{$fontSans}" font-size="10pt" margin-bottom="2mm">
          IBAN: <xsl:value-of select="xr:PAYMENT_INSTRUCTIONS/xr:Payment_card_primary_account_number_id"/>
        </fo:block>
        <fo:block font-family="{$fontSans}" font-size="10pt">
          BIC: <xsl:value-of select="xr:PAYMENT_INSTRUCTIONS/xr:Payment_card_holder_name"/>
        </fo:block>
      </fo:block-container>
    </fo:block-container>
  </xsl:template>

  <!-- Custom Invoice Details Section -->
  <xsl:template name="custom-invoice-details-section">
    <fo:block-container display-align="before" height="40mm" margin-bottom="15mm">
      <fo:block-container width="50%" float="left">
        <!-- Recipient Information from ZUGFeRD data -->
        <fo:block font-family="{$fontSans}" font-size="10pt" font-weight="bold" margin-bottom="5mm">
          Empfänger:
        </fo:block>
        <fo:block font-family="{$fontSans}" font-size="10pt" margin-bottom="2mm">
          <xsl:value-of select="xr:BUYER/xr:Buyer_name"/>
        </fo:block>
        <fo:block font-family="{$fontSans}" font-size="10pt" margin-bottom="2mm">
          <xsl:value-of select="xr:BUYER/xr:Buyer_postal_address/xr:Buyer_street_name"/>
        </fo:block>
        <fo:block font-family="{$fontSans}" font-size="10pt">
          <xsl:value-of select="xr:BUYER/xr:Buyer_postal_address/xr:Buyer_postal_code"/>
          <xsl:text> </xsl:text>
          <xsl:value-of select="xr:BUYER/xr:Buyer_postal_address/xr:Buyer_city"/>
          <xsl:text>, </xsl:text>
          <xsl:value-of select="xr:BUYER/xr:Buyer_postal_address/xr:Buyer_country_subdivision"/>
        </fo:block>
      </fo:block-container>

      <fo:block-container width="45%" float="right">
        <!-- Invoice Specifics from ZUGFeRD data -->
        <fo:table width="100%">
          <fo:table-column column-width="60%"/>
          <fo:table-column column-width="40%"/>
          <fo:table-body>
            <fo:table-row>
              <fo:table-cell>
                <fo:block font-family="{$fontSans}" font-size="10pt">Rechnungs-Nr.</fo:block>
              </fo:table-cell>
              <fo:table-cell>
                <fo:block font-family="{$fontSans}" font-size="10pt" text-align="right">
                  <xsl:value-of select="xr:Invoice_number"/>
                </fo:block>
              </fo:table-cell>
            </fo:table-row>
            <fo:table-row>
              <fo:table-cell>
                <fo:block font-family="{$fontSans}" font-size="10pt">Rechnungsdatum:</fo:block>
              </fo:table-cell>
              <fo:table-cell>
                <fo:block font-family="{$fontSans}" font-size="10pt" text-align="right">
                  <xsl:value-of select="format-date(xr:Invoice_issue_date, '[D01].[M01].[Y0001]')"/>
                </fo:block>
              </fo:table-cell>
            </fo:table-row>
            <fo:table-row>
              <fo:table-cell>
                <fo:block font-family="{$fontSans}" font-size="10pt">Leistungsdatum:</fo:block>
              </fo:table-cell>
              <fo:table-cell>
                <fo:block font-family="{$fontSans}" font-size="10pt" text-align="right">
                  <xsl:value-of select="format-date(xr:DELIVERY_INFORMATION/xr:Actual_delivery_date, '[D01].[M01].[Y0001]')"/>
                </fo:block>
              </fo:table-cell>
            </fo:table-row>
            <fo:table-row>
              <fo:table-cell>
                <fo:block font-family="{$fontSans}" font-size="10pt">Fälligkeitsdatum:</fo:block>
              </fo:table-cell>
              <fo:table-cell>
                <fo:block font-family="{$fontSans}" font-size="10pt" text-align="right">
                  <xsl:value-of select="format-date(xr:PAYMENT_INSTRUCTIONS/xr:Payment_due_date, '[D01].[M01].[Y0001]')"/>
                </fo:block>
              </fo:table-cell>
            </fo:table-row>
          </fo:table-body>
        </fo:table>
      </fo:block-container>
    </fo:block-container>
  </xsl:template>

  <!-- Custom Invoice Items Table -->
  <xsl:template name="custom-invoice-items-table">
    <fo:block font-family="{$fontSans}" font-size="16pt" font-weight="bold" text-align="center" margin-bottom="10mm">
      Rechnung
    </fo:block>

    <fo:table width="100%" border-collapse="collapse" margin-bottom="10mm">
      <fo:table-column column-width="8%"/>
      <fo:table-column column-width="40%"/>
      <fo:table-column column-width="12%"/>
      <fo:table-column column-width="12%"/>
      <fo:table-column column-width="12%"/>
      <fo:table-column column-width="16%"/>

      <!-- Table Header -->
      <fo:table-header>
        <fo:table-row background-color="#f0f0f0">
          <fo:table-cell border="0.5pt solid #000000" padding="5pt">
            <fo:block font-family="{$fontSans}" font-size="9pt" font-weight="bold" text-align="center">Position</fo:block>
          </fo:table-cell>
          <fo:table-cell border="0.5pt solid #000000" padding="5pt">
            <fo:block font-family="{$fontSans}" font-size="9pt" font-weight="bold" text-align="center">Anzahl</fo:block>
          </fo:table-cell>
          <fo:table-cell border="0.5pt solid #000000" padding="5pt">
            <fo:block font-family="{$fontSans}" font-size="9pt" font-weight="bold" text-align="center">Preis</fo:block>
          </fo:table-cell>
          <fo:table-cell border="0.5pt solid #000000" padding="5pt">
            <fo:block font-family="{$fontSans}" font-size="9pt" font-weight="bold" text-align="center">Rabatt</fo:block>
          </fo:table-cell>
          <fo:table-cell border="0.5pt solid #000000" padding="5pt">
            <fo:block font-family="{$fontSans}" font-size="9pt" font-weight="bold" text-align="center">Steuer</fo:block>
          </fo:table-cell>
          <fo:table-cell border="0.5pt solid #000000" padding="5pt">
            <fo:block font-family="{$fontSans}" font-size="9pt" font-weight="bold" text-align="center">Gesamt</fo:block>
          </fo:table-cell>
        </fo:table-row>
      </fo:table-header>

      <!-- Table Body - iterate over invoice line items -->
      <fo:table-body>
        <xsl:for-each select="xr:INVOICE_LINE">
          <fo:table-row>
            <fo:table-cell border="0.5pt solid #000000" padding="5pt">
              <fo:block font-family="{$fontSans}" font-size="9pt" text-align="center">
                <xsl:value-of select="position()"/>
              </fo:block>
            </fo:table-cell>
            <fo:table-cell border="0.5pt solid #000000" padding="5pt">
              <fo:block font-family="{$fontSans}" font-size="9pt">
                <xsl:value-of select="xr:Invoice_line_item/xr:Item_name"/>
              </fo:block>
            </fo:table-cell>
            <fo:table-cell border="0.5pt solid #000000" padding="5pt">
              <fo:block font-family="{$fontSans}" font-size="9pt" text-align="right">
                <xsl:value-of select="xr:Invoiced_quantity"/>
              </fo:block>
            </fo:table-cell>
            <fo:table-cell border="0.5pt solid #000000" padding="5pt">
              <fo:block font-family="{$fontSans}" font-size="9pt" text-align="right">
                <xsl:value-of select="format-number(xr:Invoice_line_net_amount, '#,##0.00')"/>
                <xsl:text> </xsl:text>
                <xsl:value-of select="xr:Invoice_line_net_amount/@currencyID"/>
              </fo:block>
            </fo:table-cell>
            <fo:table-cell border="0.5pt solid #000000" padding="5pt">
              <fo:block font-family="{$fontSans}" font-size="9pt" text-align="right">
                <xsl:value-of select="format-number(xr:Invoice_line_allowance_charge/xr:Allowance_charge_percentage, '#0')"/>
                <xsl:text>%</xsl:text>
              </fo:block>
            </fo:table-cell>
            <fo:table-cell border="0.5pt solid #000000" padding="5pt">
              <fo:block font-family="{$fontSans}" font-size="9pt" text-align="right">
                <xsl:value-of select="format-number(xr:Invoice_line_net_amount, '#,##0.00')"/>
                <xsl:text> </xsl:text>
                <xsl:value-of select="xr:Invoice_line_net_amount/@currencyID"/>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>
        </xsl:for-each>
      </fo:table-body>
    </fo:table>
  </xsl:template>

  <!-- Custom Amount Summary -->
  <xsl:template name="custom-amount-summary">
    <fo:block-container width="40%" margin-left="60%" margin-bottom="15mm">
      <fo:table width="100%">
        <fo:table-column column-width="60%"/>
        <fo:table-column column-width="40%"/>
        <fo:table-body>
          <fo:table-row>
            <fo:table-cell padding="3pt">
              <fo:block font-family="{$fontSans}" font-size="10pt">Nettobetrag:</fo:block>
            </fo:table-cell>
            <fo:table-cell padding="3pt">
              <fo:block font-family="{$fontSans}" font-size="10pt" text-align="right">
                <xsl:value-of select="format-number(xr:LEGAL_MONETARY_TOTAL/xr:Line_extension_amount, '#,##0.00')"/>
                <xsl:text> </xsl:text>
                <xsl:value-of select="xr:LEGAL_MONETARY_TOTAL/xr:Line_extension_amount/@currencyID"/>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>
          <fo:table-row>
            <fo:table-cell padding="3pt">
              <fo:block font-family="{$fontSans}" font-size="10pt">Steuer 19%:</fo:block>
            </fo:table-cell>
            <fo:table-cell padding="3pt">
              <fo:block font-family="{$fontSans}" font-size="10pt" text-align="right">
                <xsl:value-of select="format-number(xr:LEGAL_MONETARY_TOTAL/xr:Tax_exclusive_amount, '#,##0.00')"/>
                <xsl:text> </xsl:text>
                <xsl:value-of select="xr:LEGAL_MONETARY_TOTAL/xr:Tax_exclusive_amount/@currencyID"/>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>
          <fo:table-row background-color="#f0f0f0">
            <fo:table-cell padding="5pt">
              <fo:block font-family="{$fontSans}" font-size="11pt" font-weight="bold">Rechnungsbetrag:</fo:block>
            </fo:table-cell>
            <fo:table-cell padding="5pt">
              <fo:block font-family="{$fontSans}" font-size="11pt" font-weight="bold" text-align="right">
                <xsl:value-of select="format-number(xr:LEGAL_MONETARY_TOTAL/xr:Tax_inclusive_amount, '#,##0.00')"/>
                <xsl:text> </xsl:text>
                <xsl:value-of select="xr:LEGAL_MONETARY_TOTAL/xr:Tax_inclusive_amount/@currencyID"/>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>
        </fo:table-body>
      </fo:table>
    </fo:block-container>
  </xsl:template>

  <!-- Custom Notes Section -->
  <xsl:template name="custom-notes-section">
    <fo:block-container width="60%">
      <fo:block font-family="{$fontSans}" font-size="10pt" font-weight="bold" margin-bottom="5mm">
        Hinweise und Anmerkungen
      </fo:block>
      <fo:block font-family="{$fontSans}" font-size="10pt">
        <xsl:value-of select="xr:PAYMENT_INSTRUCTIONS/xr:Payment_terms"/>
      </fo:block>
    </fo:block-container>
  </xsl:template>

</xsl:stylesheet>
