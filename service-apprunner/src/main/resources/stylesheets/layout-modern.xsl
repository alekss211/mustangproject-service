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
            Seite <fo:page-number/> | Erstellt am <xsl:value-of select="format-date(current-date(), '[D01].[M01].[Y0001]')"/>
          </fo:block>
        </fo:static-content>
        
        <fo:flow flow-name="xsl-region-body">
          
          <!-- Modern Header with Color Accent -->
          <fo:block-container background-color="#2c3e50" color="white" padding="15pt" margin-bottom="20mm">
            <fo:table width="100%">
              <fo:table-column column-width="70%"/>
              <fo:table-column column-width="30%"/>
              <fo:table-body>
                <fo:table-row>
                  <fo:table-cell>
                    <fo:block font-size="28pt" font-weight="bold">RECHNUNG</fo:block>
                    <fo:block font-size="12pt" margin-top="5pt">
                      Nr. <xsl:value-of select="rsm:ExchangedDocument/ram:ID"/>
                    </fo:block>
                  </fo:table-cell>
                  <fo:table-cell text-align="right">
                    <fo:block font-size="14pt" font-weight="bold">
                      <xsl:value-of select="rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:Name"/>
                    </fo:block>
                  </fo:table-cell>
                </fo:table-row>
              </fo:table-body>
            </fo:table>
          </fo:block-container>
          
          <!-- Business Info Cards -->
          <fo:table width="100%" margin-bottom="20mm">
            <fo:table-column column-width="48%"/>
            <fo:table-column column-width="4%"/>
            <fo:table-column column-width="48%"/>
            <fo:table-body>
              <fo:table-row>
                <!-- Sender Card -->
                <fo:table-cell border="1pt solid #ecf0f1" background-color="#f8f9fa" padding="10pt">
                  <fo:block font-weight="bold" color="#2c3e50" margin-bottom="5pt">RECHNUNGSSTELLER</fo:block>
                  <fo:block><xsl:value-of select="rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:Name"/></fo:block>
                  <fo:block><xsl:value-of select="rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:PostalTradeAddress/ram:LineOne"/></fo:block>
                </fo:table-cell>
                <fo:table-cell><fo:block></fo:block></fo:table-cell>
                <!-- Recipient Card -->
                <fo:table-cell border="1pt solid #ecf0f1" background-color="#f8f9fa" padding="10pt">
                  <fo:block font-weight="bold" color="#2c3e50" margin-bottom="5pt">RECHNUNGSEMPFÄNGER</fo:block>
                  <fo:block><xsl:value-of select="rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:Name"/></fo:block>
                  <fo:block><xsl:value-of select="rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:PostalTradeAddress/ram:LineOne"/></fo:block>
                </fo:table-cell>
              </fo:table-row>
            </fo:table-body>
          </fo:table>
          
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
                    <fo:block text-align="right"><xsl:value-of select="format-number(ram:SpecifiedLineTradeAgreement/ram:NetPriceProductTradePrice/ram:ChargeAmount, '0,00')"/> €</fo:block>
                  </fo:table-cell>
                  <fo:table-cell padding="8pt" border="0.5pt solid #dee2e6">
                    <fo:block text-align="right" font-weight="bold"><xsl:value-of select="format-number(ram:SpecifiedLineTradeSettlement/ram:SpecifiedTradeSettlementLineMonetarySummation/ram:LineTotalAmount, '0,00')"/> €</fo:block>
                  </fo:table-cell>
                </fo:table-row>
              </xsl:for-each>
            </fo:table-body>
          </fo:table>
          
          <!-- Modern Total Section -->
          <fo:block-container margin-top="15mm" width="60%" margin-left="40%">
            <fo:table width="100%">
              <fo:table-column column-width="60%"/>
              <fo:table-column column-width="40%"/>
              <fo:table-body>
                <fo:table-row>
                  <fo:table-cell padding="5pt">
                    <fo:block>Nettobetrag:</fo:block>
                  </fo:table-cell>
                  <fo:table-cell padding="5pt" text-align="right">
                    <fo:block><xsl:value-of select="format-number(rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementHeaderMonetarySummation/ram:TaxBasisTotalAmount, '0,00')"/> €</fo:block>
                  </fo:table-cell>
                </fo:table-row>
                <fo:table-row>
                  <fo:table-cell padding="5pt">
                    <fo:block>MwSt. 19%:</fo:block>
                  </fo:table-cell>
                  <fo:table-cell padding="5pt" text-align="right">
                    <fo:block><xsl:value-of select="format-number(rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:ApplicableTradeTax/ram:CalculatedAmount, '0,00')"/> €</fo:block>
                  </fo:table-cell>
                </fo:table-row>
                <fo:table-row background-color="#2c3e50" color="white">
                  <fo:table-cell padding="8pt">
                    <fo:block font-weight="bold" font-size="12pt">GESAMTBETRAG:</fo:block>
                  </fo:table-cell>
                  <fo:table-cell padding="8pt" text-align="right">
                    <fo:block font-weight="bold" font-size="12pt"><xsl:value-of select="format-number(rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementHeaderMonetarySummation/ram:GrandTotalAmount, '0,00')"/> €</fo:block>
                  </fo:table-cell>
                </fo:table-row>
              </fo:table-body>
            </fo:table>
          </fo:block-container>
          
        </fo:flow>
      </fo:page-sequence>
    </fo:root>
  </xsl:template>
</xsl:stylesheet>
