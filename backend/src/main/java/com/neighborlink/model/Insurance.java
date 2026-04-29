package com.neighborlink.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Insurance {
    private Integer    policyId;
    private Integer    rentalId;
    private BigDecimal premiumAmt;
    private BigDecimal coverageLimit;

    public Insurance() {}

    public Integer    getPolicyId()                { return policyId; }
    public void       setPolicyId(Integer v)       { this.policyId = v; }
    public Integer    getRentalId()                { return rentalId; }
    public void       setRentalId(Integer v)       { this.rentalId = v; }
    public BigDecimal getPremiumAmt()              { return premiumAmt; }
    public void       setPremiumAmt(BigDecimal v)  { this.premiumAmt = v; }
    public BigDecimal getCoverageLimit()           { return coverageLimit; }
    public void       setCoverageLimit(BigDecimal v){ this.coverageLimit = v; }
}
