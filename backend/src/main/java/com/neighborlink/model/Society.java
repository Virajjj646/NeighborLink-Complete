package com.neighborlink.model;

public class Society {
    private Integer socId;
    private String  socName;
    private String  city;
    private String  address;

    public Society() {}
    public Society(Integer socId, String socName, String city, String address) {
        this.socId = socId; this.socName = socName;
        this.city = city;   this.address = address;
    }
    public Integer getSocId()             { return socId; }
    public void    setSocId(Integer v)    { this.socId = v; }
    public String  getSocName()           { return socName; }
    public void    setSocName(String v)   { this.socName = v; }
    public String  getCity()              { return city; }
    public void    setCity(String v)      { this.city = v; }
    public String  getAddress()           { return address; }
    public void    setAddress(String v)   { this.address = v; }
}
