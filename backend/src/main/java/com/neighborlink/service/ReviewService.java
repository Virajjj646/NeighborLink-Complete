package com.neighborlink.service;

import com.neighborlink.dao.ReviewDAO;
import com.neighborlink.exception.BadRequestException;
import com.neighborlink.model.Review;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ReviewService {

    private final ReviewDAO reviewDAO;
    public ReviewService(ReviewDAO reviewDAO) { this.reviewDAO = reviewDAO; }

    public Map<String,Object> create(Review r) {
        if (r.getRating() == null || r.getRating() < 1 || r.getRating() > 5)
            throw new BadRequestException("Rating must be between 1 and 5 (CHECK constraint)");
        int id = reviewDAO.create(r);
        return Map.of("success", true, "reviewId", id,
                      "message", "Review submitted. Trust score trigger fired automatically.");
    }

    public List<Review>           getAll()              { return reviewDAO.findAll(); }
    public List<Map<String,Object>> getAllDetailed()    { return reviewDAO.findAllDetailed(); }
    public Map<String,Object>     getTrustStats(int id) { return reviewDAO.getOwnerTrustStats(id); }
    public Map<String,Object>     delete(int id)        {
        reviewDAO.delete(id);
        return Map.of("success", true, "message", "Review deleted");
    }
}
