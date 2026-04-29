package com.neighborlink.service;

import com.neighborlink.dao.ProductDAO;
import com.neighborlink.exception.*;
import com.neighborlink.model.Product;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
public class ProductService {

    private final ProductDAO productDAO;
    public ProductService(ProductDAO productDAO) { this.productDAO = productDAO; }

    public Map<String,Object> create(Product p) {
        if (p.getTitle() == null || p.getTitle().isBlank())
            throw new BadRequestException("Title is required");
        if (p.getPricePerDay() == null || p.getPricePerDay().compareTo(BigDecimal.ZERO) <= 0)
            throw new BadRequestException("Price per day must be positive");
        int id = productDAO.create(p);
        return Map.of("success", true, "productId", id, "message", "Listing created");
    }

    public List<Product>          getAll()                    { return productDAO.findAll(); }
    public Product                getById(int id)             {
        return productDAO.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }
    public List<Product>          getByOwner(int ownerId)     { return productDAO.findByOwner(ownerId); }
    public Map<String,Object>     getStats()                  { return productDAO.getStats(); }
    public List<Map<String,Object>> getWithOwners()           { return productDAO.findWithOwners(); }
    public List<Map<String,Object>> getLeftJoin()             { return productDAO.findWithOwnersLeftJoin(); }
    public List<String>           getAboveAverage()           { return productDAO.findAboveAveragePrice(); }
    public List<Map<String,Object>> getMultiJoin()            { return productDAO.multiJoinRentals(); }
    public List<Map<String,Object>> getRentalCounts()         { return productDAO.rentalCountPerProduct(); }

    public Map<String,Object> checkAvailability(int productId, LocalDate start, LocalDate end) {
        boolean avail = productDAO.isAvailable(productId, start, end);
        return Map.of("productId", productId, "startDate", start,
                      "endDate", end, "available", avail);
    }

    public Map<String,Object> updatePrice(int id, BigDecimal price) {
        productDAO.updatePrice(id, price);
        return Map.of("success", true, "message", "Price updated");
    }

    public Map<String,Object> delete(int id) {
        productDAO.delete(id);
        return Map.of("success", true, "message", "Product deleted");
    }
}
