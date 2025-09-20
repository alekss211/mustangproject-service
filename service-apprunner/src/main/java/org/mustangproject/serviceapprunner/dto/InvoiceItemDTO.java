package org.mustangproject.serviceapprunner.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InvoiceItemDTO {
    
    @JsonProperty("item_row_number")
    private Integer itemRowNumber;
    
    @JsonProperty("item_description")
    private String itemDescription;
    
    @JsonProperty("item_quantity")
    private Double itemQuantity;
    
    @JsonProperty("item_price")
    private Double itemPrice;
    
    @JsonProperty("item_discount")
    private Double itemDiscount;
    
    @JsonProperty("item_tax")
    private Double itemTax;
    
    @JsonProperty("item_line_total")
    private Double itemLineTotal;
    
    // Getters and setters
    public Integer getItemRowNumber() { return itemRowNumber; }
    public void setItemRowNumber(Integer itemRowNumber) { this.itemRowNumber = itemRowNumber; }
    
    public String getItemDescription() { return itemDescription; }
    public void setItemDescription(String itemDescription) { this.itemDescription = itemDescription; }
    
    public Double getItemQuantity() { return itemQuantity; }
    public void setItemQuantity(Double itemQuantity) { this.itemQuantity = itemQuantity; }
    
    public Double getItemPrice() { return itemPrice; }
    public void setItemPrice(Double itemPrice) { this.itemPrice = itemPrice; }
    
    public Double getItemDiscount() { return itemDiscount; }
    public void setItemDiscount(Double itemDiscount) { this.itemDiscount = itemDiscount; }
    
    public Double getItemTax() { return itemTax; }
    public void setItemTax(Double itemTax) { this.itemTax = itemTax; }
    
    public Double getItemLineTotal() { return itemLineTotal; }
    public void setItemLineTotal(Double itemLineTotal) { this.itemLineTotal = itemLineTotal; }
}
