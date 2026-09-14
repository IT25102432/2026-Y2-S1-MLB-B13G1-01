package com.cinema.movie_reservation_system.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "loyalty_account")
public class LoyaltyAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_number")
    private Long accountNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false, unique = true)
    private Customer customer;

    @Column(name = "created_date", nullable = false)
    private LocalDate createdDate;

    @Column(length = 50)
    private String tier = "BRONZE";

    @Column(name = "points_balance")
    private Integer pointsBalance = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_manager_id")
    private CinemaManager createdByManager;

    // getters/setters
    public Long getAccountNumber() { return accountNumber; }
    public void setAccountNumber(Long accountNumber) { this.accountNumber = accountNumber; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public LocalDate getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDate createdDate) { this.createdDate = createdDate; }
    public String getTier() { return tier; }
    public void setTier(String tier) { this.tier = tier; }
    public Integer getPointsBalance() { return pointsBalance; }
    public void setPointsBalance(Integer pointsBalance) { this.pointsBalance = pointsBalance; }
    public CinemaManager getCreatedByManager() { return createdByManager; }
    public void setCreatedByManager(CinemaManager createdByManager) { this.createdByManager = createdByManager; }
}