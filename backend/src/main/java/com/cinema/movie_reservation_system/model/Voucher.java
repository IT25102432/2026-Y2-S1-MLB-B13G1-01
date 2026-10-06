package com.cinema.movie_reservation_system.model;

/**
 * Model representing a Discount Voucher in the loyalty program.
 * Part of Loyalty Program & Discount Vouchers (IT24100907).
 */
public class Voucher {

    private Long id;
    private String code;
    private Double discountAmountLkr;
    private boolean isActive = true;

    public Voucher() {
    }

    public Voucher(String code, Double discountAmountLkr, boolean isActive) {
        this.code = code != null ? code.toUpperCase().trim() : null;
        this.discountAmountLkr = discountAmountLkr;
        this.isActive = isActive;
    }

    public Voucher(Long id, String code, Double discountAmountLkr, boolean isActive) {
        this.id = id;
        this.code = code != null ? code.toUpperCase().trim() : null;
        this.discountAmountLkr = discountAmountLkr;
        this.isActive = isActive;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code != null ? code.toUpperCase().trim() : null;
    }

    public Double getDiscountAmountLkr() {
        return discountAmountLkr;
    }

    public void setDiscountAmountLkr(Double discountAmountLkr) {
        this.discountAmountLkr = discountAmountLkr;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "Voucher{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", discountAmountLkr=" + discountAmountLkr +
                ", isActive=" + isActive +
                '}';
    }
}
