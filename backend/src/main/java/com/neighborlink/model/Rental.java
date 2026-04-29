package com.neighborlink.model;

import java.time.LocalDate;

public class Rental {
    private Integer   rentalId;
    private Integer   productId;
    private Integer   renterId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String    status;
    // JOIN fields
    private String    renterName;
    private String    ownerName;
    private String    productTitle;

    public Rental() {}

    public Integer   getRentalId()               { return rentalId; }
    public void      setRentalId(Integer v)      { this.rentalId = v; }
    public Integer   getProductId()              { return productId; }
    public void      setProductId(Integer v)     { this.productId = v; }
    public Integer   getRenterId()               { return renterId; }
    public void      setRenterId(Integer v)      { this.renterId = v; }
    public LocalDate getStartDate()              { return startDate; }
    public void      setStartDate(LocalDate v)   { this.startDate = v; }
    public LocalDate getEndDate()                { return endDate; }
    public void      setEndDate(LocalDate v)     { this.endDate = v; }
    public String    getStatus()                 { return status; }
    public void      setStatus(String v)         { this.status = v; }
    public String    getRenterName()             { return renterName; }
    public void      setRenterName(String v)     { this.renterName = v; }
    public String    getOwnerName()              { return ownerName; }
    public void      setOwnerName(String v)      { this.ownerName = v; }
    public String    getProductTitle()           { return productTitle; }
    public void      setProductTitle(String v)   { this.productTitle = v; }
}
