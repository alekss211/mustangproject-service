package org.mustangproject.serviceapprunner.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class InvoiceDTO {
    
    // Company information
    @JsonProperty("company_logo")
    private String companyLogo;
    
    @JsonProperty("company_name")
    private String companyName;
    
    @JsonProperty("company_info1")
    private String companyInfo1;
    
    @JsonProperty("company_info2")
    private String companyInfo2;
    
    @JsonProperty("company_info3")
    private String companyInfo3;
    
    @JsonProperty("company_info4")
    private String companyInfo4;
    
    @JsonProperty("company_info5")
    private String companyInfo5;
    
    @JsonProperty("company_info6")
    private String companyInfo6;
    
    @JsonProperty("company_info7")
    private String companyInfo7;
    
    @JsonProperty("company_info8")
    private String companyInfo8;
    
    @JsonProperty("company_info9")
    private String companyInfo9;
    
    // Invoice labels
    @JsonProperty("invoice_date_label")
    private String invoiceDateLabel;
    
    @JsonProperty("issue_date_label")
    private String issueDateLabel;
    
    @JsonProperty("due_date_label")
    private String dueDateLabel;
    
    @JsonProperty("currency_label")
    private String currencyLabel;
    
    @JsonProperty("invoice_info1_label")
    private String invoiceInfo1Label;
    
    @JsonProperty("invoice_info1")
    private String invoiceInfo1;
    
    @JsonProperty("invoice_info2_label")
    private String invoiceInfo2Label;
    
    @JsonProperty("invoice_info2")
    private String invoiceInfo2;
    
    @JsonProperty("invoice_info3_label")
    private String invoiceInfo3Label;
    
    @JsonProperty("invoice_info3")
    private String invoiceInfo3;
    
    @JsonProperty("invoice_info4_label")
    private String invoiceInfo4Label;
    
    @JsonProperty("invoice_info4")
    private String invoiceInfo4;
    
    @JsonProperty("invoice_info5_label")
    private String invoiceInfo5Label;
    
    @JsonProperty("invoice_info5")
    private String invoiceInfo5;
    
    // Bill to information
    @JsonProperty("bill_to_label")
    private String billToLabel;
    
    @JsonProperty("bill_to_info1")
    private String billToInfo1;
    
    @JsonProperty("bill_to_info2")
    private String billToInfo2;
    
    @JsonProperty("bill_to_info3")
    private String billToInfo3;
    
    @JsonProperty("bill_to_info4")
    private String billToInfo4;
    
    @JsonProperty("bill_to_info5")
    private String billToInfo5;
    
    // Invoice details
    @JsonProperty("invoice_title")
    private String invoiceTitle;
    
    @JsonProperty("invoice_number")
    private String invoiceNumber;
    
    @JsonProperty("invoice_number_label")
    private String invoiceNumberLabel;
    
    // Item labels
    @JsonProperty("item_row_number_label")
    private String itemRowNumberLabel;
    
    @JsonProperty("item_description_label")
    private String itemDescriptionLabel;
    
    @JsonProperty("item_quantity_label")
    private String itemQuantityLabel;
    
    @JsonProperty("item_price_label")
    private String itemPriceLabel;
    
    @JsonProperty("item_discount_label")
    private String itemDiscountLabel;
    
    @JsonProperty("item_tax_label")
    private String itemTaxLabel;
    
    @JsonProperty("item_line_total_label")
    private String itemLineTotalLabel;
    
    // Amounts
    @JsonProperty("amount_subtotal_label")
    private String amountSubtotalLabel;
    
    @JsonProperty("amount_subtotal")
    private Double amountSubtotal;
    
    @JsonProperty("amount_total_label")
    private String amountTotalLabel;
    
    @JsonProperty("amount_total")
    private Double amountTotal;
    
    @JsonProperty("amount_paid_label")
    private String amountPaidLabel;
    
    @JsonProperty("amount_paid")
    private Double amountPaid;
    
    @JsonProperty("amount_due_label")
    private String amountDueLabel;
    
    @JsonProperty("amount_due")
    private Double amountDue;
    
    // Terms
    @JsonProperty("terms_label")
    private String termsLabel;
    
    @JsonProperty("terms")
    private String terms;
    
    // Currency and formatting
    @JsonProperty("date_format")
    private String dateFormat;
    
    @JsonProperty("currency_code")
    private String currencyCode;
    
    @JsonProperty("currency_symbol")
    private String currencySymbol;
    
    @JsonProperty("currency_position")
    private String currencyPosition;
    
    @JsonProperty("currency_number_format")
    private String currencyNumberFormat;
    
    @JsonProperty("num_fraction_digits")
    private Integer numFractionDigits;
    
    @JsonProperty("fraction_sep")
    private String fractionSep;
    
    @JsonProperty("thousands_sep")
    private String thousandsSep;
    
    // Items and columns
    @JsonProperty("items_columns")
    private List<String> itemsColumns;
    
    @JsonProperty("items")
    private List<InvoiceItemDTO> items;
    
    // Dates
    @JsonProperty("invoice_date")
    private String invoiceDate;
    
    @JsonProperty("issue_date")
    private String issueDate;
    
    @JsonProperty("due_date")
    private String dueDate;
    
    // Template and taxes
    @JsonProperty("template_name")
    private String templateName;
    
    @JsonProperty("taxes")
    private List<TaxDTO> taxes;
    
    // Getters and setters
    public String getCompanyLogo() { return companyLogo; }
    public void setCompanyLogo(String companyLogo) { this.companyLogo = companyLogo; }
    
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    
    public String getCompanyInfo1() { return companyInfo1; }
    public void setCompanyInfo1(String companyInfo1) { this.companyInfo1 = companyInfo1; }
    
    public String getCompanyInfo2() { return companyInfo2; }
    public void setCompanyInfo2(String companyInfo2) { this.companyInfo2 = companyInfo2; }
    
    public String getCompanyInfo3() { return companyInfo3; }
    public void setCompanyInfo3(String companyInfo3) { this.companyInfo3 = companyInfo3; }
    
    public String getCompanyInfo4() { return companyInfo4; }
    public void setCompanyInfo4(String companyInfo4) { this.companyInfo4 = companyInfo4; }
    
    public String getCompanyInfo5() { return companyInfo5; }
    public void setCompanyInfo5(String companyInfo5) { this.companyInfo5 = companyInfo5; }
    
    public String getCompanyInfo6() { return companyInfo6; }
    public void setCompanyInfo6(String companyInfo6) { this.companyInfo6 = companyInfo6; }
    
    public String getCompanyInfo7() { return companyInfo7; }
    public void setCompanyInfo7(String companyInfo7) { this.companyInfo7 = companyInfo7; }
    
    public String getCompanyInfo8() { return companyInfo8; }
    public void setCompanyInfo8(String companyInfo8) { this.companyInfo8 = companyInfo8; }
    
    public String getCompanyInfo9() { return companyInfo9; }
    public void setCompanyInfo9(String companyInfo9) { this.companyInfo9 = companyInfo9; }
    
    public String getInvoiceDateLabel() { return invoiceDateLabel; }
    public void setInvoiceDateLabel(String invoiceDateLabel) { this.invoiceDateLabel = invoiceDateLabel; }
    
    public String getIssueDateLabel() { return issueDateLabel; }
    public void setIssueDateLabel(String issueDateLabel) { this.issueDateLabel = issueDateLabel; }
    
    public String getDueDateLabel() { return dueDateLabel; }
    public void setDueDateLabel(String dueDateLabel) { this.dueDateLabel = dueDateLabel; }
    
    public String getCurrencyLabel() { return currencyLabel; }
    public void setCurrencyLabel(String currencyLabel) { this.currencyLabel = currencyLabel; }
    
    public String getInvoiceInfo1Label() { return invoiceInfo1Label; }
    public void setInvoiceInfo1Label(String invoiceInfo1Label) { this.invoiceInfo1Label = invoiceInfo1Label; }
    
    public String getInvoiceInfo1() { return invoiceInfo1; }
    public void setInvoiceInfo1(String invoiceInfo1) { this.invoiceInfo1 = invoiceInfo1; }
    
    public String getInvoiceInfo2Label() { return invoiceInfo2Label; }
    public void setInvoiceInfo2Label(String invoiceInfo2Label) { this.invoiceInfo2Label = invoiceInfo2Label; }
    
    public String getInvoiceInfo2() { return invoiceInfo2; }
    public void setInvoiceInfo2(String invoiceInfo2) { this.invoiceInfo2 = invoiceInfo2; }
    
    public String getInvoiceInfo3Label() { return invoiceInfo3Label; }
    public void setInvoiceInfo3Label(String invoiceInfo3Label) { this.invoiceInfo3Label = invoiceInfo3Label; }
    
    public String getInvoiceInfo3() { return invoiceInfo3; }
    public void setInvoiceInfo3(String invoiceInfo3) { this.invoiceInfo3 = invoiceInfo3; }
    
    public String getInvoiceInfo4Label() { return invoiceInfo4Label; }
    public void setInvoiceInfo4Label(String invoiceInfo4Label) { this.invoiceInfo4Label = invoiceInfo4Label; }
    
    public String getInvoiceInfo4() { return invoiceInfo4; }
    public void setInvoiceInfo4(String invoiceInfo4) { this.invoiceInfo4 = invoiceInfo4; }
    
    public String getInvoiceInfo5Label() { return invoiceInfo5Label; }
    public void setInvoiceInfo5Label(String invoiceInfo5Label) { this.invoiceInfo5Label = invoiceInfo5Label; }
    
    public String getInvoiceInfo5() { return invoiceInfo5; }
    public void setInvoiceInfo5(String invoiceInfo5) { this.invoiceInfo5 = invoiceInfo5; }
    
    public String getBillToLabel() { return billToLabel; }
    public void setBillToLabel(String billToLabel) { this.billToLabel = billToLabel; }
    
    public String getBillToInfo1() { return billToInfo1; }
    public void setBillToInfo1(String billToInfo1) { this.billToInfo1 = billToInfo1; }
    
    public String getBillToInfo2() { return billToInfo2; }
    public void setBillToInfo2(String billToInfo2) { this.billToInfo2 = billToInfo2; }
    
    public String getBillToInfo3() { return billToInfo3; }
    public void setBillToInfo3(String billToInfo3) { this.billToInfo3 = billToInfo3; }
    
    public String getBillToInfo4() { return billToInfo4; }
    public void setBillToInfo4(String billToInfo4) { this.billToInfo4 = billToInfo4; }
    
    public String getBillToInfo5() { return billToInfo5; }
    public void setBillToInfo5(String billToInfo5) { this.billToInfo5 = billToInfo5; }
    
    public String getInvoiceTitle() { return invoiceTitle; }
    public void setInvoiceTitle(String invoiceTitle) { this.invoiceTitle = invoiceTitle; }
    
    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    
    public String getInvoiceNumberLabel() { return invoiceNumberLabel; }
    public void setInvoiceNumberLabel(String invoiceNumberLabel) { this.invoiceNumberLabel = invoiceNumberLabel; }
    
    public String getItemRowNumberLabel() { return itemRowNumberLabel; }
    public void setItemRowNumberLabel(String itemRowNumberLabel) { this.itemRowNumberLabel = itemRowNumberLabel; }
    
    public String getItemDescriptionLabel() { return itemDescriptionLabel; }
    public void setItemDescriptionLabel(String itemDescriptionLabel) { this.itemDescriptionLabel = itemDescriptionLabel; }
    
    public String getItemQuantityLabel() { return itemQuantityLabel; }
    public void setItemQuantityLabel(String itemQuantityLabel) { this.itemQuantityLabel = itemQuantityLabel; }
    
    public String getItemPriceLabel() { return itemPriceLabel; }
    public void setItemPriceLabel(String itemPriceLabel) { this.itemPriceLabel = itemPriceLabel; }
    
    public String getItemDiscountLabel() { return itemDiscountLabel; }
    public void setItemDiscountLabel(String itemDiscountLabel) { this.itemDiscountLabel = itemDiscountLabel; }
    
    public String getItemTaxLabel() { return itemTaxLabel; }
    public void setItemTaxLabel(String itemTaxLabel) { this.itemTaxLabel = itemTaxLabel; }
    
    public String getItemLineTotalLabel() { return itemLineTotalLabel; }
    public void setItemLineTotalLabel(String itemLineTotalLabel) { this.itemLineTotalLabel = itemLineTotalLabel; }
    
    public String getAmountSubtotalLabel() { return amountSubtotalLabel; }
    public void setAmountSubtotalLabel(String amountSubtotalLabel) { this.amountSubtotalLabel = amountSubtotalLabel; }
    
    public Double getAmountSubtotal() { return amountSubtotal; }
    public void setAmountSubtotal(Double amountSubtotal) { this.amountSubtotal = amountSubtotal; }
    
    public String getAmountTotalLabel() { return amountTotalLabel; }
    public void setAmountTotalLabel(String amountTotalLabel) { this.amountTotalLabel = amountTotalLabel; }
    
    public Double getAmountTotal() { return amountTotal; }
    public void setAmountTotal(Double amountTotal) { this.amountTotal = amountTotal; }
    
    public String getAmountPaidLabel() { return amountPaidLabel; }
    public void setAmountPaidLabel(String amountPaidLabel) { this.amountPaidLabel = amountPaidLabel; }
    
    public Double getAmountPaid() { return amountPaid; }
    public void setAmountPaid(Double amountPaid) { this.amountPaid = amountPaid; }
    
    public String getAmountDueLabel() { return amountDueLabel; }
    public void setAmountDueLabel(String amountDueLabel) { this.amountDueLabel = amountDueLabel; }
    
    public Double getAmountDue() { return amountDue; }
    public void setAmountDue(Double amountDue) { this.amountDue = amountDue; }
    
    public String getTermsLabel() { return termsLabel; }
    public void setTermsLabel(String termsLabel) { this.termsLabel = termsLabel; }
    
    public String getTerms() { return terms; }
    public void setTerms(String terms) { this.terms = terms; }
    
    public String getDateFormat() { return dateFormat; }
    public void setDateFormat(String dateFormat) { this.dateFormat = dateFormat; }
    
    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
    
    public String getCurrencySymbol() { return currencySymbol; }
    public void setCurrencySymbol(String currencySymbol) { this.currencySymbol = currencySymbol; }
    
    public String getCurrencyPosition() { return currencyPosition; }
    public void setCurrencyPosition(String currencyPosition) { this.currencyPosition = currencyPosition; }
    
    public String getCurrencyNumberFormat() { return currencyNumberFormat; }
    public void setCurrencyNumberFormat(String currencyNumberFormat) { this.currencyNumberFormat = currencyNumberFormat; }
    
    public Integer getNumFractionDigits() { return numFractionDigits; }
    public void setNumFractionDigits(Integer numFractionDigits) { this.numFractionDigits = numFractionDigits; }
    
    public String getFractionSep() { return fractionSep; }
    public void setFractionSep(String fractionSep) { this.fractionSep = fractionSep; }
    
    public String getThousandsSep() { return thousandsSep; }
    public void setThousandsSep(String thousandsSep) { this.thousandsSep = thousandsSep; }
    
    public List<String> getItemsColumns() { return itemsColumns; }
    public void setItemsColumns(List<String> itemsColumns) { this.itemsColumns = itemsColumns; }
    
    public List<InvoiceItemDTO> getItems() { return items; }
    public void setItems(List<InvoiceItemDTO> items) { this.items = items; }
    
    public String getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(String invoiceDate) { this.invoiceDate = invoiceDate; }
    
    public String getIssueDate() { return issueDate; }
    public void setIssueDate(String issueDate) { this.issueDate = issueDate; }
    
    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    
    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }
    
    public List<TaxDTO> getTaxes() { return taxes; }
    public void setTaxes(List<TaxDTO> taxes) { this.taxes = taxes; }
}
