<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:fo="http://www.w3.org/1999/XSL/Format" 
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:axf="http://www.antennahouse.com/names/XSL/Extensions"
                version="2.0">

  <!-- Import the main ZUGFeRD content -->
  <xsl:import href="../library/src/main/resources/stylesheets/xr-content.xsl"/>
  <xsl:import href="../library/src/main/resources/stylesheets/common-xr.xsl"/>

  <!-- Custom page layout for the invoice -->
  <xsl:template name="generiere-layout-master-set">
    <fo:layout-master-set>
      <fo:simple-page-master master-name="InvoicePage" 
                             page-height="297mm" 
                             page-width="210mm">
        <fo:region-body region-name="body"
                        margin="20mm 15mm 20mm 15mm"/>
      </fo:simple-page-master>

      <fo:page-sequence-master master-name="InvoiceSequence">
        <fo:single-page-master-reference master-reference="InvoicePage"/>
      </fo:page-sequence-master>
    </fo:layout-master-set>
  </xsl:template>

  <!-- Main invoice template -->
  <xsl:template match="/">
    <fo:root>
      <xsl:call-template name="generiere-layout-master-set"/>
      
      <fo:page-sequence master-reference="InvoiceSequence">
        <fo:flow flow-name="body">
          <!-- Header Section -->
          <xsl:call-template name="header-section"/>
          
          <!-- Invoice Details Section -->
          <xsl:call-template name="invoice-details-section"/>
          
          <!-- Invoice Items Table -->
          <xsl:call-template name="invoice-items-table"/>
          
          <!-- Amount Summary -->
          <xsl:call-template name="amount-summary"/>
          
          <!-- Notes Section -->
          <xsl:call-template name="notes-section"/>
          
        </fo:flow>
      </fo:page-sequence>
    </fo:root>
  </xsl:template>

  <!-- Header Section: Company Info (left) + Payment Details (right) -->
  <xsl:template name="header-section">
    <fo:block-container display-align="before" height="60mm" margin-bottom="10mm">
      <fo:block-container width="50%" float="left">
        <!-- Company Information -->
        <fo:block font-family="Arial" font-size="18pt" font-weight="bold" margin-bottom="5mm">
          Musterfirma
        </fo:block>
        <fo:block font-family="Arial" font-size="10pt" margin-bottom="2mm">
          Musterstraße 123, 12345 Musterstadt
        </fo:block>
        <fo:block font-family="Arial" font-size="10pt" margin-bottom="2mm">
          www.musterfirma.de
        </fo:block>
        <fo:block font-family="Arial" font-size="10pt" margin-bottom="2mm">
          kontakt@musterfirma.de
        </fo:block>
        <fo:block font-family="Arial" font-size="10pt">
          Ust.-ID: DE1234567890
        </fo:block>
      </fo:block-container>

      <fo:block-container width="45%" float="right">
        <!-- Payment Details -->
        <fo:block font-family="Arial" font-size="10pt" font-weight="bold" margin-bottom="5mm">
          Zahlungsdetails:
        </fo:block>
        <fo:block font-family="Arial" font-size="10pt" margin-bottom="2mm">
          Kontoinhaber: Musterfirma
        </fo:block>
        <fo:block font-family="Arial" font-size="10pt" margin-bottom="2mm">
          IBAN: DE12345678912345678912
        </fo:block>
        <fo:block font-family="Arial" font-size="10pt">
          BIC: ABCDEFGH
        </fo:block>
      </fo:block-container>
    </fo:block-container>
  </xsl:template>

  <!-- Invoice Details Section -->
  <xsl:template name="invoice-details-section">
    <fo:block-container display-align="before" height="40mm" margin-bottom="15mm">
      <fo:block-container width="50%" float="left">
        <!-- Recipient Information -->
        <fo:block font-family="Arial" font-size="10pt" font-weight="bold" margin-bottom="5mm">
          Empfänger:
        </fo:block>
        <fo:block font-family="Arial" font-size="10pt" margin-bottom="2mm">
          Max Mustermann
        </fo:block>
        <fo:block font-family="Arial" font-size="10pt" margin-bottom="2mm">
          Musterstraße 123
        </fo:block>
        <fo:block font-family="Arial" font-size="10pt">
          12345 Musterstadt, Deutschland
        </fo:block>
      </fo:block-container>

      <fo:block-container width="45%" float="right">
        <!-- Invoice Specifics -->
        <fo:table width="100%">
          <fo:table-column column-width="60%"/>
          <fo:table-column column-width="40%"/>
          <fo:table-body>
            <fo:table-row>
              <fo:table-cell>
                <fo:block font-family="Arial" font-size="10pt">Rechnungs-Nr.</fo:block>
              </fo:table-cell>
              <fo:table-cell>
                <fo:block font-family="Arial" font-size="10pt" text-align="right">1</fo:block>
              </fo:table-cell>
            </fo:table-row>
            <fo:table-row>
              <fo:table-cell>
                <fo:block font-family="Arial" font-size="10pt">Rechnungsdatum:</fo:block>
              </fo:table-cell>
              <fo:table-cell>
                <fo:block font-family="Arial" font-size="10pt" text-align="right">08.09.2025</fo:block>
              </fo:table-cell>
            </fo:table-row>
            <fo:table-row>
              <fo:table-cell>
                <fo:block font-family="Arial" font-size="10pt">Leistungsdatum:</fo:block>
              </fo:table-cell>
              <fo:table-cell>
                <fo:block font-family="Arial" font-size="10pt" text-align="right">08.09.2025</fo:block>
              </fo:table-cell>
            </fo:table-row>
            <fo:table-row>
              <fo:table-cell>
                <fo:block font-family="Arial" font-size="10pt">Fälligkeitsdatum:</fo:block>
              </fo:table-cell>
              <fo:table-cell>
                <fo:block font-family="Arial" font-size="10pt" text-align="right">08.10.2025</fo:block>
              </fo:table-cell>
            </fo:table-row>
          </fo:table-body>
        </fo:table>
      </fo:block-container>
    </fo:block-container>
  </xsl:template>

  <!-- Invoice Items Table -->
  <xsl:template name="invoice-items-table">
    <fo:block font-family="Arial" font-size="16pt" font-weight="bold" text-align="center" margin-bottom="10mm">
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
            <fo:block font-family="Arial" font-size="9pt" font-weight="bold" text-align="center">Position</fo:block>
          </fo:table-cell>
          <fo:table-cell border="0.5pt solid #000000" padding="5pt">
            <fo:block font-family="Arial" font-size="9pt" font-weight="bold" text-align="center">Anzahl</fo:block>
          </fo:table-cell>
          <fo:table-cell border="0.5pt solid #000000" padding="5pt">
            <fo:block font-family="Arial" font-size="9pt" font-weight="bold" text-align="center">Preis</fo:block>
          </fo:table-cell>
          <fo:table-cell border="0.5pt solid #000000" padding="5pt">
            <fo:block font-family="Arial" font-size="9pt" font-weight="bold" text-align="center">Rabatt</fo:block>
          </fo:table-cell>
          <fo:table-cell border="0.5pt solid #000000" padding="5pt">
            <fo:block font-family="Arial" font-size="9pt" font-weight="bold" text-align="center">Steuer</fo:block>
          </fo:table-cell>
          <fo:table-cell border="0.5pt solid #000000" padding="5pt">
            <fo:block font-family="Arial" font-size="9pt" font-weight="bold" text-align="center">Gesamt</fo:block>
          </fo:table-cell>
        </fo:table-row>
      </fo:table-header>

      <!-- Table Body -->
      <fo:table-body>
        <fo:table-row>
          <fo:table-cell border="0.5pt solid #000000" padding="5pt">
            <fo:block font-family="Arial" font-size="9pt" text-align="center">1</fo:block>
          </fo:table-cell>
          <fo:table-cell border="0.5pt solid #000000" padding="5pt">
            <fo:block font-family="Arial" font-size="9pt">Musterleistung</fo:block>
          </fo:table-cell>
          <fo:table-cell border="0.5pt solid #000000" padding="5pt">
            <fo:block font-family="Arial" font-size="9pt" text-align="right">1</fo:block>
          </fo:table-cell>
          <fo:table-cell border="0.5pt solid #000000" padding="5pt">
            <fo:block font-family="Arial" font-size="9pt" text-align="right">100,00 €</fo:block>
          </fo:table-cell>
          <fo:table-cell border="0.5pt solid #000000" padding="5pt">
            <fo:block font-family="Arial" font-size="9pt" text-align="right">0%</fo:block>
          </fo:table-cell>
          <fo:table-cell border="0.5pt solid #000000" padding="5pt">
            <fo:block font-family="Arial" font-size="9pt" text-align="right">19%</fo:block>
          </fo:table-cell>
          <fo:table-cell border="0.5pt solid #000000" padding="5pt">
            <fo:block font-family="Arial" font-size="9pt" text-align="right">100,00 €</fo:block>
          </fo:table-cell>
        </fo:table-row>
      </fo:table-body>
    </fo:table>
  </xsl:template>

  <!-- Amount Summary -->
  <xsl:template name="amount-summary">
    <fo:block-container width="40%" margin-left="60%" margin-bottom="15mm">
      <fo:table width="100%">
        <fo:table-column column-width="60%"/>
        <fo:table-column column-width="40%"/>
        <fo:table-body>
          <fo:table-row>
            <fo:table-cell padding="3pt">
              <fo:block font-family="Arial" font-size="10pt">Nettobetrag:</fo:block>
            </fo:table-cell>
            <fo:table-cell padding="3pt">
              <fo:block font-family="Arial" font-size="10pt" text-align="right">100,00 €</fo:block>
            </fo:table-cell>
          </fo:table-row>
          <fo:table-row>
            <fo:table-cell padding="3pt">
              <fo:block font-family="Arial" font-size="10pt">Steuer 19%:</fo:block>
            </fo:table-cell>
            <fo:table-cell padding="3pt">
              <fo:block font-family="Arial" font-size="10pt" text-align="right">19,00 €</fo:block>
            </fo:table-cell>
          </fo:table-row>
          <fo:table-row background-color="#f0f0f0">
            <fo:table-cell padding="5pt">
              <fo:block font-family="Arial" font-size="11pt" font-weight="bold">Rechnungsbetrag:</fo:block>
            </fo:table-cell>
            <fo:table-cell padding="5pt">
              <fo:block font-family="Arial" font-size="11pt" font-weight="bold" text-align="right">119,00 €</fo:block>
            </fo:table-cell>
          </fo:table-row>
        </fo:table-body>
      </fo:table>
    </fo:block-container>
  </xsl:template>

  <!-- Notes Section -->
  <xsl:template name="notes-section">
    <fo:block-container width="60%">
      <fo:block font-family="Arial" font-size="10pt" font-weight="bold" margin-bottom="5mm">
        Hinweise und Anmerkungen
      </fo:block>
      <fo:block font-family="Arial" font-size="10pt">
        Vielen Dank für Ihren Auftrag. Bitte senden Sie Zahlungen vor dem Fälligkeitsdatum.
      </fo:block>
    </fo:block-container>
  </xsl:template>

</xsl:stylesheet>
