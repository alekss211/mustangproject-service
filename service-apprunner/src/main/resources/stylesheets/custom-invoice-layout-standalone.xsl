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
  <xsl:param name="tabular-layout-widths">1 4 1 1.5 1 1 1.5</xsl:param>
  <xsl:param name="axf.extensions" select="false()"/>
  <xsl:param name="fop.extensions" select="true()"/>

  <!-- Font and styling variables -->
  <xsl:variable name="fontSans">Arial, sans-serif</xsl:variable>
  <xsl:variable name="fontSerif">Times, serif</xsl:variable>
  <xsl:variable name="lang">de</xsl:variable>

  <!-- Main invoice template -->
  <xsl:template match="xr:invoice">
    <fo:root xmlns:pdf="http://xmlgraphics.apache.org/fop/extensions/pdf"
      language="{$lang}" font-family="{$fontSans}">
      
      <!-- Layout master set -->
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

      <!-- Page sequence -->
      <fo:page-sequence master-reference="xrDokument">
        <fo:flow flow-name="xrBody">
          
          <!-- Header with company info -->
          <fo:block-container height="60mm" margin-bottom="10mm">
            <fo:table table-layout="fixed" width="100%">
              <fo:table-column column-width="50%"/>
              <fo:table-column column-width="50%"/>
              <fo:table-body>
                <fo:table-row>
                  <!-- Left side - Company name -->
                  <fo:table-cell>
                    <fo:block font-size="18pt" font-weight="bold" margin-bottom="5mm">
                      <xsl:value-of select="//xr:SellerTradeParty/xr:Name"/>
                    </fo:block>
                    <fo:block font-size="10pt" line-height="1.4">
                      <xsl:value-of select="//xr:SellerTradeParty/xr:PostalTradeAddress/xr:LineOne"/>
                    </fo:block>
                    <fo:block font-size="10pt" line-height="1.4">
                      <xsl:value-of select="//xr:SellerTradeParty/xr:PostalTradeAddress/xr:LineTwo"/>
                    </fo:block>
                    <fo:block font-size="10pt" line-height="1.4">
                      <xsl:value-of select="//xr:SellerTradeParty/xr:URIUniversalCommunication/xr:URIID"/>
                    </fo:block>
                    <fo:block font-size="10pt" line-height="1.4">
                      <xsl:value-of select="//xr:SellerTradeParty/xr:SpecifiedTaxRegistration/xr:ID"/>
                    </fo:block>
                  </fo:table-cell>
                  
                  <!-- Right side - Payment details -->
                  <fo:table-cell text-align="right">
                    <fo:block font-size="10pt" font-weight="bold" margin-bottom="3mm">
                      Zahlungsdetails:
                    </fo:block>
                    <fo:block font-size="9pt" line-height="1.4">
                      Kontoinhaber: <xsl:value-of select="//xr:SellerTradeParty/xr:Name"/>
                    </fo:block>
                    <fo:block font-size="9pt" line-height="1.4">
                      IBAN: DE12345678912345678912
                    </fo:block>
                    <fo:block font-size="9pt" line-height="1.4">
                      BIC: ABCDEFGH
                    </fo:block>
                  </fo:table-cell>
                </fo:table-row>
              </fo:table-body>
            </fo:table>
          </fo:block-container>

          <!-- Recipient and Invoice details -->
          <fo:block-container margin-bottom="15mm">
            <fo:table table-layout="fixed" width="100%">
              <fo:table-column column-width="50%"/>
              <fo:table-column column-width="50%"/>
              <fo:table-body>
                <fo:table-row>
                  <!-- Left side - Recipient -->
                  <fo:table-cell>
                    <fo:block font-size="10pt" font-weight="bold" margin-bottom="3mm">
                      Empfänger:
                    </fo:block>
                    <fo:block font-size="10pt" line-height="1.4">
                      <xsl:value-of select="//xr:BuyerTradeParty/xr:Name"/>
                    </fo:block>
                    <fo:block font-size="10pt" line-height="1.4">
                      <xsl:value-of select="//xr:BuyerTradeParty/xr:PostalTradeAddress/xr:LineOne"/>
                    </fo:block>
                    <fo:block font-size="10pt" line-height="1.4">
                      <xsl:value-of select="//xr:BuyerTradeParty/xr:PostalTradeAddress/xr:PostcodeCode"/>
                      <xsl:text> </xsl:text>
                      <xsl:value-of select="//xr:BuyerTradeParty/xr:PostalTradeAddress/xr:CityName"/>
                      <xsl:text>, </xsl:text>
                      <xsl:value-of select="//xr:BuyerTradeParty/xr:PostalTradeAddress/xr:CountryID"/>
                    </fo:block>
                  </fo:table-cell>
                  
                  <!-- Right side - Invoice details -->
                  <fo:table-cell text-align="right">
                    <fo:table table-layout="fixed" width="100%">
                      <fo:table-column column-width="60%"/>
                      <fo:table-column column-width="40%"/>
                      <fo:table-body>
                        <fo:table-row>
                          <fo:table-cell><fo:block font-size="10pt">Rechnungs-Nr.</fo:block></fo:table-cell>
                          <fo:table-cell text-align="right">
                            <fo:block font-size="10pt">
                              <xsl:value-of select="//xr:ExchangedDocument/xr:ID"/>
                            </fo:block>
                          </fo:table-cell>
                        </fo:table-row>
                        <fo:table-row>
                          <fo:table-cell><fo:block font-size="10pt">Rechnungsdatum:</fo:block></fo:table-cell>
                          <fo:table-cell text-align="right">
                            <fo:block font-size="10pt">
                              <xsl:value-of select="format-date(xs:date(//xr:ExchangedDocument/xr:IssueDateTime/xr:DateTimeString), '[D01].[M01].[Y0001]')"/>
                            </fo:block>
                          </fo:table-cell>
                        </fo:table-row>
                        <fo:table-row>
                          <fo:table-cell><fo:block font-size="10pt">Leistungsdatum:</fo:block></fo:table-cell>
                          <fo:table-cell text-align="right">
                            <fo:block font-size="10pt">
                              <xsl:value-of select="format-date(xs:date(//xr:SupplyChainTradeTransaction/xr:ApplicableHeaderTradeDelivery/xr:ActualDeliverySupplyChainEvent/xr:OccurrenceDateTime/xr:DateTimeString), '[D01].[M01].[Y0001]')"/>
                            </fo:block>
                          </fo:table-cell>
                        </fo:table-row>
                        <fo:table-row>
                          <fo:table-cell><fo:block font-size="10pt">Fälligkeitsdatum:</fo:block></fo:table-cell>
                          <fo:table-cell text-align="right">
                            <fo:block font-size="10pt">
                              <xsl:value-of select="format-date(xs:date(//xr:SupplyChainTradeTransaction/xr:ApplicableHeaderTradeSettlement/xr:SpecifiedTradePaymentTerms/xr:DueDateDateTime/xr:DateTimeString), '[D01].[M01].[Y0001]')"/>
                            </fo:block>
                          </fo:table-cell>
                        </fo:table-row>
                      </fo:table-body>
                    </fo:table>
                  </fo:table-cell>
                </fo:table-row>
              </fo:table-body>
            </fo:table>
          </fo:block-container>

          <!-- Invoice title -->
          <fo:block font-size="16pt" font-weight="bold" margin-bottom="10mm">
            Rechnung
          </fo:block>

          <!-- Items table -->
          <fo:table table-layout="fixed" width="100%" border="0.5pt solid black" margin-bottom="10mm">
            <fo:table-column column-width="8%"/>   <!-- Position -->
            <fo:table-column column-width="40%"/>  <!-- Description -->
            <fo:table-column column-width="10%"/>  <!-- Anzahl -->
            <fo:table-column column-width="12%"/>  <!-- Preis -->
            <fo:table-column column-width="10%"/>  <!-- Rabatt -->
            <fo:table-column column-width="10%"/>  <!-- Steuer -->
            <fo:table-column column-width="10%"/>  <!-- Gesamt -->
            
            <!-- Table header -->
            <fo:table-header>
              <fo:table-row background-color="#f0f0f0">
                <fo:table-cell border="0.5pt solid black" padding="3pt">
                  <fo:block font-weight="bold" font-size="9pt"></fo:block>
                </fo:table-cell>
                <fo:table-cell border="0.5pt solid black" padding="3pt">
                  <fo:block font-weight="bold" font-size="9pt">Position</fo:block>
                </fo:table-cell>
                <fo:table-cell border="0.5pt solid black" padding="3pt" text-align="center">
                  <fo:block font-weight="bold" font-size="9pt">Anzahl</fo:block>
                </fo:table-cell>
                <fo:table-cell border="0.5pt solid black" padding="3pt" text-align="right">
                  <fo:block font-weight="bold" font-size="9pt">Preis</fo:block>
                </fo:table-cell>
                <fo:table-cell border="0.5pt solid black" padding="3pt" text-align="center">
                  <fo:block font-weight="bold" font-size="9pt">Rabatt</fo:block>
                </fo:table-cell>
                <fo:table-cell border="0.5pt solid black" padding="3pt" text-align="center">
                  <fo:block font-weight="bold" font-size="9pt">Steuer</fo:block>
                </fo:table-cell>
                <fo:table-cell border="0.5pt solid black" padding="3pt" text-align="right">
                  <fo:block font-weight="bold" font-size="9pt">Gesamt</fo:block>
                </fo:table-cell>
              </fo:table-row>
            </fo:table-header>
            
            <!-- Table body -->
            <fo:table-body>
              <xsl:for-each select="//xr:SupplyChainTradeTransaction/xr:IncludedSupplyChainTradeLineItem">
                <fo:table-row>
                  <fo:table-cell border="0.5pt solid black" padding="3pt" text-align="center">
                    <fo:block font-size="9pt">
                      <xsl:value-of select="position()"/>
                    </fo:block>
                  </fo:table-cell>
                  <fo:table-cell border="0.5pt solid black" padding="3pt">
                    <fo:block font-size="9pt">
                      <xsl:value-of select="xr:SpecifiedTradeProduct/xr:Name"/>
                    </fo:block>
                  </fo:table-cell>
                  <fo:table-cell border="0.5pt solid black" padding="3pt" text-align="center">
                    <fo:block font-size="9pt">
                      <xsl:value-of select="xr:SpecifiedLineTradeDelivery/xr:BilledQuantity"/>
                    </fo:block>
                  </fo:table-cell>
                  <fo:table-cell border="0.5pt solid black" padding="3pt" text-align="right">
                    <fo:block font-size="9pt">
                      <xsl:value-of select="format-number(xr:SpecifiedLineTradeAgreement/xr:NetPriceProductTradePrice/xr:ChargeAmount, '0,00')"/> €
                    </fo:block>
                  </fo:table-cell>
                  <fo:table-cell border="0.5pt solid black" padding="3pt" text-align="center">
                    <fo:block font-size="9pt">0%</fo:block>
                  </fo:table-cell>
                  <fo:table-cell border="0.5pt solid black" padding="3pt" text-align="center">
                    <fo:block font-size="9pt">
                      <xsl:value-of select="xr:SpecifiedLineTradeSettlement/xr:ApplicableTradeTax/xr:RateApplicablePercent"/>%
                    </fo:block>
                  </fo:table-cell>
                  <fo:table-cell border="0.5pt solid black" padding="3pt" text-align="right">
                    <fo:block font-size="9pt">
                      <xsl:value-of select="format-number(xr:SpecifiedLineTradeSettlement/xr:SpecifiedTradeSettlementLineMonetarySummation/xr:LineTotalAmount, '0,00')"/> €
                    </fo:block>
                  </fo:table-cell>
                </fo:table-row>
              </xsl:for-each>
            </fo:table-body>
          </fo:table>

          <!-- Totals section -->
          <fo:block-container margin-bottom="15mm">
            <fo:table table-layout="fixed" width="100%">
              <fo:table-column column-width="60%"/>
              <fo:table-column column-width="40%"/>
              <fo:table-body>
                <fo:table-row>
                  <fo:table-cell>
                    <fo:block></fo:block>
                  </fo:table-cell>
                  <fo:table-cell>
                    <fo:table table-layout="fixed" width="100%">
                      <fo:table-column column-width="60%"/>
                      <fo:table-column column-width="40%"/>
                      <fo:table-body>
                        <!-- Net amount -->
                        <fo:table-row>
                          <fo:table-cell padding="2pt">
                            <fo:block font-size="10pt">Nettobetrag:</fo:block>
                          </fo:table-cell>
                          <fo:table-cell text-align="right" padding="2pt">
                            <fo:block font-size="10pt">
                              <xsl:value-of select="format-number(//xr:SupplyChainTradeTransaction/xr:ApplicableHeaderTradeSettlement/xr:SpecifiedTradeSettlementHeaderMonetarySummation/xr:TaxBasisTotalAmount, '0,00')"/> €
                            </fo:block>
                          </fo:table-cell>
                        </fo:table-row>
                        
                        <!-- Tax -->
                        <fo:table-row>
                          <fo:table-cell padding="2pt">
                            <fo:block font-size="10pt">
                              Steuer <xsl:value-of select="//xr:SupplyChainTradeTransaction/xr:ApplicableHeaderTradeSettlement/xr:ApplicableTradeTax/xr:RateApplicablePercent"/>%:
                            </fo:block>
                          </fo:table-cell>
                          <fo:table-cell text-align="right" padding="2pt">
                            <fo:block font-size="10pt">
                              <xsl:value-of select="format-number(//xr:SupplyChainTradeTransaction/xr:ApplicableHeaderTradeSettlement/xr:ApplicableTradeTax/xr:CalculatedAmount, '0,00')"/> €
                            </fo:block>
                          </fo:table-cell>
                        </fo:table-row>
                        
                        <!-- Total amount -->
                        <fo:table-row border-top="1pt solid black">
                          <fo:table-cell padding="2pt">
                            <fo:block font-size="11pt" font-weight="bold">Rechnungsbetrag:</fo:block>
                          </fo:table-cell>
                          <fo:table-cell text-align="right" padding="2pt">
                            <fo:block font-size="11pt" font-weight="bold">
                              <xsl:value-of select="format-number(//xr:SupplyChainTradeTransaction/xr:ApplicableHeaderTradeSettlement/xr:SpecifiedTradeSettlementHeaderMonetarySummation/xr:GrandTotalAmount, '0,00')"/> €
                            </fo:block>
                          </fo:table-cell>
                        </fo:table-row>
                      </fo:table-body>
                    </fo:table>
                  </fo:table-cell>
                </fo:table-row>
              </fo:table-body>
            </fo:table>
          </fo:block-container>

          <!-- Terms and notes -->
          <fo:block font-size="10pt" font-weight="bold" margin-bottom="3mm">
            Hinweise und Anmerkungen
          </fo:block>
          <fo:block font-size="10pt" line-height="1.4">
            <xsl:value-of select="//xr:SupplyChainTradeTransaction/xr:ApplicableHeaderTradeSettlement/xr:SpecifiedTradePaymentTerms/xr:Description"/>
          </fo:block>

        </fo:flow>
      </fo:page-sequence>
    </fo:root>
  </xsl:template>

</xsl:stylesheet>