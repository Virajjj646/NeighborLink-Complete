package com.neighborlink.model;

import java.time.LocalDateTime;

public class Review {
    private Integer       reviewId;
    private Integer       rentalId;
    private Integer       reviewerId;
    private Integer       rating;
    private String        comment;
    private LocalDateTime reviewDate;
    // JOIN fields
    private String        reviewerName;
    private String        productTitle;

    public Review() {}

    public Integer       getReviewId()               { return reviewId; }
    public void          setReviewId(Integer v)      { this.reviewId = v; }
    public Integer       getRentalId()               { return rentalId; }
    public void          setRentalId(Integer v)      { this.rentalId = v; }
    public Integer       getReviewerId()             { return reviewerId; }
    public void          setReviewerId(Integer v)    { this.reviewerId = v; }
    public Integer       getRating()                 { return rating; }
    public void          setRating(Integer v)        { this.rating = v; }
    public String        getComment()                { return comment; }
    public void          setComment(String v)        { this.comment = v; }
    public LocalDateTime getReviewDate()             { return reviewDate; }
    public void          setReviewDate(LocalDateTime v){ this.reviewDate = v; }
    public String        getReviewerName()           { return reviewerName; }
    public void          setReviewerName(String v)   { this.reviewerName = v; }
    public String        getProductTitle()           { return productTitle; }
    public void          setProductTitle(String v)   { this.productTitle = v; }
}
