package org.mustangproject.serviceapprunner.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TaxDTO {
    
    @JsonProperty("tax_name")
    private String taxName;
    
    @JsonProperty("tax_value")
    private Double taxValue;
    
    @JsonProperty("tax_percentage")
    private Double taxPercentage;
    
    // Getters and setters
    public String getTaxName() { return taxName; }
    public void setTaxName(String taxName) { this.taxName = taxName; }
    
    public Double getTaxValue() { return taxValue; }
    public void setTaxValue(Double taxValue) { this.taxValue = taxValue; }
    
    public Double getTaxPercentage() { return taxPercentage; }
    public void setTaxPercentage(Double taxPercentage) { this.taxPercentage = taxPercentage; }
}
