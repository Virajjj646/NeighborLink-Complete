package com.neighborlink.model;

import java.math.BigDecimal;

public class User {
    private Integer    userId;
    private Integer    socId;
    private String     username;
    private String     email;
    private String     flatNo;
    private BigDecimal trustScore;
    private String     password;

    public User() {}

    public Integer    getUserId()               { return userId; }
    public void       setUserId(Integer v)      { this.userId = v; }
    public Integer    getSocId()                { return socId; }
    public void       setSocId(Integer v)       { this.socId = v; }
    public String     getUsername()             { return username; }
    public void       setUsername(String v)     { this.username = v; }
    public String     getEmail()                { return email; }
    public void       setEmail(String v)        { this.email = v; }
    public String     getFlatNo()               { return flatNo; }
    public void       setFlatNo(String v)       { this.flatNo = v; }
    public BigDecimal getTrustScore()           { return trustScore; }
    public void       setTrustScore(BigDecimal v){ this.trustScore = v; }
    public String     getPassword()             { return password; }
    public void       setPassword(String v)     { this.password = v; }
}
