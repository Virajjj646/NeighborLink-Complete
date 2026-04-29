package com.neighborlink.model;

import java.math.BigDecimal;

public class Product {
    private Integer    productId;
    private Integer    ownerId;
    private String     title;
    private String     description;
    private BigDecimal pricePerDay;
    private BigDecimal itemValue;
    // Extra fields from JOIN queries
    private String     ownerName;

    public Product() {}

    public Integer    getProductId()              { return productId; }
    public void       setProductId(Integer v)     { this.productId = v; }
    public Integer    getOwnerId()                { return ownerId; }
    public void       setOwnerId(Integer v)       { this.ownerId = v; }
    public String     getTitle()                  { return title; }
    public void       setTitle(String v)          { this.title = v; }
    public String     getDescription()            { return description; }
    public void       setDescription(String v)    { this.description = v; }
    public BigDecimal getPricePerDay()            { return pricePerDay; }
    public void       setPricePerDay(BigDecimal v){ this.pricePerDay = v; }
    public BigDecimal getItemValue()              { return itemValue; }
    public void       setItemValue(BigDecimal v)  { this.itemValue = v; }
    public String     getOwnerName()              { return ownerName; }
    public void       setOwnerName(String v)      { this.ownerName = v; }
}
