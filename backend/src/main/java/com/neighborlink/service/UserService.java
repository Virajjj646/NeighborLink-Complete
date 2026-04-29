package com.neighborlink.service;

import com.neighborlink.dao.UserDAO;
import com.neighborlink.exception.*;
import com.neighborlink.model.User;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    private final UserDAO userDAO;
    public UserService(UserDAO userDAO) { this.userDAO = userDAO; }

    public Map<String,Object> register(User user) {
        // basic validation
        if (user.getEmail() == null || user.getEmail().isBlank())
            throw new BadRequestException("Email is required");
        if (user.getUsername() == null || user.getUsername().isBlank())
            throw new BadRequestException("Username is required");
        if (user.getPassword() == null || user.getPassword().isBlank())
            throw new BadRequestException("Password is required");

        int id = userDAO.register(user);
        return Map.of("success", true, "userId", id, "message", "User registered successfully");
    }

    public Map<String,Object> login(String email, String password) {
        User user = userDAO.findByEmailAndPassword(email, password)
            .orElseThrow(() -> new BadRequestException("Invalid email or password"));
        Map<String,Object> resp = new LinkedHashMap<>();
        resp.put("success", true);
        resp.put("userId",     user.getUserId());
        resp.put("username",   user.getUsername());
        resp.put("socId",      user.getSocId());
        resp.put("trustScore", user.getTrustScore());
        resp.put("flatNo",     user.getFlatNo());
        return resp;
    }

    public User getById(int id) {
        return userDAO.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    public List<User> getAll()                         { return userDAO.findAll(); }
    public int        countAll()                       { return userDAO.countAll(); }
    public List<String> getRenters()                   { return userDAO.findUsernamesWhoRented(); }
    public List<String> getNonRenters()                { return userDAO.findUsernamesWhoNeverRented(); }
    public List<String> getUnionUsernamesProducts()    { return userDAO.unionUsernamesAndProducts(); }
    public Map<String,Object> getTrustInfo(int id)     { return userDAO.getTrustScoreInfo(id); }
    public int callTotalRentalsFunction(int id)        { return userDAO.callTotalRentalsByUser(id); }
}
