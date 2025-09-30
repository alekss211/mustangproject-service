<?xml version="1.0" encoding="UTF-8"?>
<!-- MINIMAL CLEAN LAYOUT -->
<xsl:stylesheet xmlns:fo="http://www.w3.org/1999/XSL/Format" 
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:rsm="urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100"
                xmlns:ram="urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100"
                xmlns:udt="urn:un:unece:uncefact:data:standard:UnqualifiedDataType:100"
                version="2.0">

  <xsl:output method="xml" version="1.0" encoding="utf-8" /> 
  <xsl:param name="foengine"/>

  <xsl:template match="rsm:CrossIndustryInvoice">
    <fo:root font-family="Arial, sans-serif">
      <fo:layout-master-set>
        <fo:simple-page-master master-name="minimal" page-height="297mm" page-width="210mm">
          <fo:region-body margin="15mm"/>
        </fo:simple-page-master>
      </fo:layout-master-set>

      <fo:page-sequence master-reference="minimal">
        <fo:flow flow-name="xsl-region-body">
          
          <!-- Minimal Header -->
          <fo:block font-size="24pt" font-weight="bold" margin-bottom="20mm" text-align="center">
            RECHNUNG
          </fo:block>
          
          <!-- Simple Info Block -->
          <fo:table width="100%" margin-bottom="15mm">
            <fo:table-column column-width="50%"/>
            <fo:table-column column-width="50%"/>
            <fo:table-body>
              <fo:table-row>
                <fo:table-cell>
                  <fo:block font-weight="bold">Von:</fo:block>
                  <fo:block><xsl:value-of select="rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:Name"/></fo:block>
                </fo:table-cell>
                <fo:table-cell>
                  <fo:block font-weight="bold">An:</fo:block>
                  <fo:block><xsl:value-of select="rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:Name"/></fo:block>
                </fo:table-cell>
              </fo:table-row>
            </fo:table-body>
          </fo:table>
          
          <!-- Simple Items -->
          <fo:table width="100%" border="1pt solid black">
            <fo:table-column column-width="60%"/>
            <fo:table-column column-width="20%"/>
            <fo:table-column column-width="20%"/>
            <fo:table-header>
              <fo:table-row background-color="#f0f0f0">
                <fo:table-cell border="1pt solid black" padding="5pt">
                  <fo:block font-weight="bold">Beschreibung</fo:block>
                </fo:table-cell>
                <fo:table-cell border="1pt solid black" padding="5pt">
                  <fo:block font-weight="bold">Menge</fo:block>
                </fo:table-cell>
                <fo:table-cell border="1pt solid black" padding="5pt">
                  <fo:block font-weight="bold">Betrag</fo:block>
                </fo:table-cell>
              </fo:table-row>
            </fo:table-header>
            <fo:table-body>
              <xsl:for-each select="rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem">
                <fo:table-row>
                  <fo:table-cell border="1pt solid black" padding="5pt">
                    <fo:block><xsl:value-of select="ram:SpecifiedTradeProduct/ram:Name"/></fo:block>
                  </fo:table-cell>
                  <fo:table-cell border="1pt solid black" padding="5pt">
                    <fo:block><xsl:value-of select="ram:SpecifiedLineTradeDelivery/ram:BilledQuantity"/></fo:block>
                  </fo:table-cell>
                  <fo:table-cell border="1pt solid black" padding="5pt" text-align="right">
                    <fo:block><xsl:value-of select="format-number(ram:SpecifiedLineTradeSettlement/ram:SpecifiedTradeSettlementLineMonetarySummation/ram:LineTotalAmount, '0,00')"/> €</fo:block>
                  </fo:table-cell>
                </fo:table-row>
              </xsl:for-each>
            </fo:table-body>
          </fo:table>
          
          <!-- Simple Total -->
          <fo:block text-align="right" margin-top="10mm" font-size="14pt" font-weight="bold">
            Gesamt: <xsl:value-of select="format-number(rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementHeaderMonetarySummation/ram:GrandTotalAmount, '0,00')"/> €
          </fo:block>
          
        </fo:flow>
      </fo:page-sequence>
    </fo:root>
  </xsl:template>
</xsl:stylesheet>
