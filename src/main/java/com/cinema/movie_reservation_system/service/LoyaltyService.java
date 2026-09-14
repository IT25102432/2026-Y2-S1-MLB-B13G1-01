package com.cinema.movie_reservation_system.service;

import com.cinema.movie_reservation_system.model.Customer;
import com.cinema.movie_reservation_system.model.LoyaltyAccount;
import com.cinema.movie_reservation_system.repository.CustomerRepository;
import com.cinema.movie_reservation_system.repository.LoyaltyAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class LoyaltyService {

    private final LoyaltyAccountRepository loyaltyAccountRepository;
    private final CustomerRepository customerRepository;

    public LoyaltyService(LoyaltyAccountRepository loyaltyAccountRepository,
                          CustomerRepository customerRepository) {
        this.loyaltyAccountRepository = loyaltyAccountRepository;
        this.customerRepository = customerRepository;
    }

    public List<LoyaltyAccount> findAll() {
        return loyaltyAccountRepository.findAll();
    }

    public LoyaltyAccount getById(Long accountNumber) {
        return loyaltyAccountRepository.findById(accountNumber)
                .orElseThrow(() -> new RuntimeException("Loyalty account not found: " + accountNumber));
    }

    @Transactional
    public LoyaltyAccount createAccount(Long customerId) {
        if (loyaltyAccountRepository.existsByCustomerUserId(customerId)) {
            throw new RuntimeException("Customer already has a loyalty account.");
        }
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerId));

        LoyaltyAccount account = new LoyaltyAccount();
        account.setCustomer(customer);
        account.setCreatedDate(LocalDate.now());
        account.setPointsBalance(0);
        account.setTier("BRONZE");
        return loyaltyAccountRepository.save(account);
    }

    /** Called by the booking flow when a booking is confirmed. */
    @Transactional
    public LoyaltyAccount earnPoints(Long customerId, int points) {
        LoyaltyAccount account = loyaltyAccountRepository.findByCustomerUserId(customerId)
                .orElseThrow(() -> new RuntimeException("No loyalty account for customer " + customerId));
        account.setPointsBalance(account.getPointsBalance() + points);
        account.setTier(computeTier(account.getPointsBalance()));
        return loyaltyAccountRepository.save(account);
    }

    /** Called at checkout when a customer applies points as a discount. */
    @Transactional
    public LoyaltyAccount redeemPoints(Long accountNumber, int points) {
        LoyaltyAccount account = getById(accountNumber);
        if (account.getPointsBalance() < points) {
            throw new RuntimeException("Insufficient points balance.");
        }
        account.setPointsBalance(account.getPointsBalance() - points);
        account.setTier(computeTier(account.getPointsBalance()));
        return loyaltyAccountRepository.save(account);
    }

    private String computeTier(int points) {
        if (points >= 5000) return "GOLD";
        if (points >= 1000) return "SILVER";
        return "BRONZE";
    }
}