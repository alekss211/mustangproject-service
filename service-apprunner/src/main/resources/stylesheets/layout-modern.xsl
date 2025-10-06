<?xml version="1.0" encoding="UTF-8"?>
<!-- MODERN BUSINESS LAYOUT -->
<xsl:stylesheet xmlns:fo="http://www.w3.org/1999/XSL/Format" 
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:rsm="urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100"
                xmlns:ram="urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100"
                xmlns:udt="urn:un:unece:uncefact:data:standard:UnqualifiedDataType:100"
                version="2.0">

  <xsl:output method="xml" version="1.0" encoding="utf-8" /> 
  <xsl:param name="foengine"/>
  
  <!-- Define German decimal format: comma as decimal separator, dot as thousands separator -->
  <xsl:decimal-format name="de" decimal-separator="," grouping-separator="." NaN="" />

  <xsl:template match="rsm:CrossIndustryInvoice">
    <fo:root font-family="SourceSansPro">
      <fo:layout-master-set>
        <fo:simple-page-master master-name="modern" page-height="297mm" page-width="210mm">
          <fo:region-body margin="20mm 15mm 25mm 15mm"/>
          <fo:region-after extent="20mm"/>
        </fo:simple-page-master>
      </fo:layout-master-set>

      <fo:page-sequence master-reference="modern">
        <fo:static-content flow-name="xsl-region-after">
          <fo:block text-align="center" font-size="8pt" color="#666666">
            <xsl:text>Seite </xsl:text>
            <fo:page-number/>
          </fo:block>
        </fo:static-content>
        
        <fo:flow flow-name="xsl-region-body">
          
          <!-- Simple Header -->
          <fo:block font-size="24pt" font-weight="bold" margin-bottom="10mm" text-align="left">
            <xsl:choose>
              <xsl:when test="rsm:ExchangedDocument/ram:IssueDateTime/udt:DateTimeString">
                <xsl:variable name="dateValue" select="rsm:ExchangedDocument/ram:IssueDateTime/udt:DateTimeString"/>
                <xsl:variable name="year" select="substring($dateValue, 1, 4)"/>
                <xsl:variable name="month" select="substring($dateValue, 5, 2)"/>
                <xsl:variable name="day" select="substring($dateValue, 7, 2)"/>
                <xsl:text>RECHNUNG vom </xsl:text>
                <xsl:value-of select="concat($day, '.', $month, '.', $year)"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:text>RECHNUNG</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
          </fo:block>
          <fo:block font-size="12pt" margin-bottom="20mm">
            Nr. <xsl:value-of select="rsm:ExchangedDocument/ram:ID"/>
          </fo:block>
          
          <!-- Business Info Cards -->
          <fo:block-container margin-bottom="20mm">
            <!-- Sender Card -->
            <fo:block border="1pt solid #ecf0f1" background-color="#f8f9fa" padding="10pt" margin-bottom="15mm">
              <fo:block font-weight="bold" color="#2c3e50" margin-bottom="5pt">RECHNUNGSSTELLER</fo:block>
              <fo:block><xsl:value-of select="rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:Name"/></fo:block>
              <fo:block><xsl:value-of select="rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:PostalTradeAddress/ram:LineOne"/></fo:block>

              <!-- Website from URIUniversalCommunication -->
              <xsl:if test="rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:URIUniversalCommunication/ram:URIID">
                <fo:block margin-top="3pt">
                  <xsl:text>Website: </xsl:text>
                  <xsl:value-of select="rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:URIUniversalCommunication/ram:URIID"/>
                </fo:block>
              </xsl:if>

              <!-- Email from DefinedTradeContact -->
              <xsl:if test="rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:DefinedTradeContact/ram:EmailURIUniversalCommunication/ram:URIID">
                <fo:block margin-top="2pt">
                  <xsl:text>E-Mail: </xsl:text>
                  <xsl:value-of select="rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:DefinedTradeContact/ram:EmailURIUniversalCommunication/ram:URIID"/>
                </fo:block>
              </xsl:if>

              <!-- VAT ID from SpecifiedTaxRegistration -->
              <xsl:if test="rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:SpecifiedTaxRegistration/ram:ID">
                <fo:block margin-top="2pt">
                  <xsl:text>USt-Id: </xsl:text>
                  <xsl:value-of select="rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:SpecifiedTaxRegistration/ram:ID"/>
                </fo:block>
              </xsl:if>
            </fo:block>

            <!-- Recipient Card -->
            <fo:block border="1pt solid #ecf0f1" background-color="#f8f9fa" padding="10pt">
              <fo:block font-weight="bold" color="#2c3e50" margin-bottom="5pt">RECHNUNGSEMPFÄNGER</fo:block>
              <fo:block><xsl:value-of select="rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:Name"/></fo:block>
              <fo:block><xsl:value-of select="rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:PostalTradeAddress/ram:LineOne"/></fo:block>
            </fo:block>
          </fo:block-container>
          
          <!-- Modern Items Table -->
          <fo:table width="100%" border-collapse="separate" border-spacing="0">
            <fo:table-column column-width="5%"/>
            <fo:table-column column-width="45%"/>
            <fo:table-column column-width="15%"/>
            <fo:table-column column-width="15%"/>
            <fo:table-column column-width="20%"/>
            
            <fo:table-header>
              <fo:table-row background-color="#34495e" color="white">
                <fo:table-cell padding="8pt" border="1pt solid #34495e">
                  <fo:block font-weight="bold" text-align="center">#</fo:block>
                </fo:table-cell>
                <fo:table-cell padding="8pt" border="1pt solid #34495e">
                  <fo:block font-weight="bold">BESCHREIBUNG</fo:block>
                </fo:table-cell>
                <fo:table-cell padding="8pt" border="1pt solid #34495e">
                  <fo:block font-weight="bold" text-align="center">MENGE</fo:block>
                </fo:table-cell>
                <fo:table-cell padding="8pt" border="1pt solid #34495e">
                  <fo:block font-weight="bold" text-align="right">PREIS</fo:block>
                </fo:table-cell>
                <fo:table-cell padding="8pt" border="1pt solid #34495e">
                  <fo:block font-weight="bold" text-align="right">GESAMT</fo:block>
                </fo:table-cell>
              </fo:table-row>
            </fo:table-header>
            
            <fo:table-body>
              <xsl:for-each select="rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem">
                <fo:table-row>
                  <xsl:if test="position() mod 2 = 0">
                    <xsl:attribute name="background-color">#f8f9fa</xsl:attribute>
                  </xsl:if>
                  <fo:table-cell padding="8pt" border="0.5pt solid #dee2e6">
                    <fo:block text-align="center"><xsl:value-of select="position()"/></fo:block>
                  </fo:table-cell>
                  <fo:table-cell padding="8pt" border="0.5pt solid #dee2e6">
                    <fo:block><xsl:value-of select="ram:SpecifiedTradeProduct/ram:Name"/></fo:block>
                  </fo:table-cell>
                  <fo:table-cell padding="8pt" border="0.5pt solid #dee2e6">
                    <fo:block text-align="center"><xsl:value-of select="ram:SpecifiedLineTradeDelivery/ram:BilledQuantity"/></fo:block>
                  </fo:table-cell>
                  <fo:table-cell padding="8pt" border="0.5pt solid #dee2e6">
                    <fo:block text-align="right"><xsl:value-of select="format-number(ram:SpecifiedLineTradeAgreement/ram:NetPriceProductTradePrice/ram:ChargeAmount, '#.##0,00', 'de')"/> €</fo:block>
                  </fo:table-cell>
                  <fo:table-cell padding="8pt" border="0.5pt solid #dee2e6">
                    <fo:block text-align="right" font-weight="bold"><xsl:value-of select="format-number(ram:SpecifiedLineTradeSettlement/ram:SpecifiedTradeSettlementLineMonetarySummation/ram:LineTotalAmount, '#.##0,00', 'de')"/> €</fo:block>
                  </fo:table-cell>
                </fo:table-row>
              </xsl:for-each>
            </fo:table-body>
          </fo:table>

          <!-- Simple Total Section -->
          <fo:table width="100%" margin-top="15mm">
            <fo:table-column column-width="70%"/>
            <fo:table-column column-width="30%"/>
            <fo:table-body>
              <fo:table-row>
                <fo:table-cell padding="8pt">
                  <fo:block font-weight="bold" font-size="14pt">GESAMTSUMME:</fo:block>
                </fo:table-cell>
                <fo:table-cell padding="8pt" text-align="right">
                  <fo:block font-weight="bold" font-size="14pt"><xsl:value-of select="format-number(rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementHeaderMonetarySummation/ram:GrandTotalAmount, '#.##0,00', 'de')"/> €</fo:block>
                </fo:table-cell>
              </fo:table-row>
            </fo:table-body>
          </fo:table>
          
          <!-- Payment Terms Section -->
          <xsl:if test="rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradePaymentTerms/ram:Description">
            <fo:block margin-top="10mm" font-size="10pt" border-top="1pt solid #dee2e6" padding-top="8pt">
              <xsl:call-template name="format-payment-terms">
                <xsl:with-param name="text" select="rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradePaymentTerms/ram:Description"/>
              </xsl:call-template>
            </fo:block>
          </xsl:if>
          
        </fo:flow>
      </fo:page-sequence>
    </fo:root>
  </xsl:template>
  
  <!-- Template to format payment terms with bold labels -->
  <xsl:template name="format-payment-terms">
    <xsl:param name="text"/>
    <xsl:call-template name="format-lines">
      <xsl:with-param name="text" select="$text"/>
    </xsl:call-template>
  </xsl:template>
  
  <xsl:template name="format-lines">
    <xsl:param name="text"/>
    <xsl:choose>
      <xsl:when test="contains($text, '&#10;')">
        <xsl:variable name="line" select="substring-before($text, '&#10;')"/>
        <xsl:if test="string-length($line) > 0">
          <fo:block>
            <xsl:call-template name="format-line">
              <xsl:with-param name="line" select="$line"/>
            </xsl:call-template>
          </fo:block>
        </xsl:if>
        <xsl:if test="string-length($line) = 0">
          <fo:block>&#160;</fo:block>
        </xsl:if>
        <xsl:call-template name="format-lines">
          <xsl:with-param name="text" select="substring-after($text, '&#10;')"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:if test="string-length($text) > 0">
          <fo:block>
            <xsl:call-template name="format-line">
              <xsl:with-param name="line" select="$text"/>
            </xsl:call-template>
          </fo:block>
        </xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template name="format-line">
    <xsl:param name="line"/>
    <xsl:choose>
      <!-- Bold label for Zahlungsinformationen -->
      <xsl:when test="starts-with($line, 'Zahlungsinformationen:')">
        <fo:inline font-weight="bold">Zahlungsinformationen:</fo:inline>
      </xsl:when>
      <!-- Bold label for Empfänger -->
      <xsl:when test="starts-with($line, 'Empfänger:')">
        <fo:inline font-weight="bold">Empfänger:</fo:inline>
        <xsl:value-of select="substring-after($line, 'Empfänger:')"/>
      </xsl:when>
      <!-- Bold label for IBAN -->
      <xsl:when test="starts-with($line, 'IBAN:')">
        <fo:inline font-weight="bold">IBAN:</fo:inline>
        <xsl:value-of select="substring-after($line, 'IBAN:')"/>
      </xsl:when>
      <!-- Bold label for BIC -->
      <xsl:when test="starts-with($line, 'BIC:')">
        <fo:inline font-weight="bold">BIC:</fo:inline>
        <xsl:value-of select="substring-after($line, 'BIC:')"/>
      </xsl:when>
      <!-- Default: just output the line as-is -->
      <xsl:otherwise>
        <xsl:value-of select="$line"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>
