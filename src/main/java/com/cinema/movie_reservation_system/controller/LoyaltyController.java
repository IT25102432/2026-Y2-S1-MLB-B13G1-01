package com.cinema.movie_reservation_system.controller;

import com.cinema.movie_reservation_system.model.LoyaltyAccount;
import com.cinema.movie_reservation_system.service.LoyaltyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/loyalty")
public class LoyaltyController {

    private final LoyaltyService loyaltyService;

    public LoyaltyController(LoyaltyService loyaltyService) {
        this.loyaltyService = loyaltyService;
    }

    @GetMapping
    public List<LoyaltyAccount> listAll() {
        return loyaltyService.findAll();
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<LoyaltyAccount> getOne(@PathVariable Long accountNumber) {
        return ResponseEntity.ok(loyaltyService.getById(accountNumber));
    }

    @PostMapping
    public ResponseEntity<LoyaltyAccount> create(@RequestBody Map<String, Object> body) {
        Long customerId = Long.valueOf(body.get("customerId").toString());
        return ResponseEntity.ok(loyaltyService.createAccount(customerId));
    }

    @PostMapping("/earn")
    public ResponseEntity<LoyaltyAccount> earn(@RequestBody Map<String, Object> body) {
        Long customerId = Long.valueOf(body.get("customerId").toString());
        int points = Integer.parseInt(body.get("points").toString());
        return ResponseEntity.ok(loyaltyService.earnPoints(customerId, points));
    }

    @PostMapping("/{accountNumber}/redeem")
    public ResponseEntity<LoyaltyAccount> redeem(@PathVariable Long accountNumber,
                                                 @RequestBody Map<String, Object> body) {
        int points = Integer.parseInt(body.get("points").toString());
        return ResponseEntity.ok(loyaltyService.redeemPoints(accountNumber, points));
    }
}