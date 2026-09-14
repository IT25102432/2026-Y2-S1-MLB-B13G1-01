package com.cinema.movie_reservation_system.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "voucher")
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "voucher_id")
    private Long voucherId;

    @Column(name = "voucher_code", nullable = false, unique = true, length = 50)
    private String voucherCode;

    @Column(name = "discount_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal discountRate;

    @Column(name = "voucher_status", length = 20)
    private String voucherStatus = "ACTIVE";

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_until", nullable = false)
    private LocalDate validUntil;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_manager_id")
    private CinemaManager createdByManager;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_staff_id")
    private BoxOfficeStaff createdByStaff;

    // getters/setters
    public Long getVoucherId() { return voucherId; }
    public void setVoucherId(Long voucherId) { this.voucherId = voucherId; }
    public String getVoucherCode() { return voucherCode; }
    public void setVoucherCode(String voucherCode) { this.voucherCode = voucherCode; }
    public BigDecimal getDiscountRate() { return discountRate; }
    public void setDiscountRate(BigDecimal discountRate) { this.discountRate = discountRate; }
    public String getVoucherStatus() { return voucherStatus; }
    public void setVoucherStatus(String voucherStatus) { this.voucherStatus = voucherStatus; }
    public LocalDate getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDate validFrom) { this.validFrom = validFrom; }
    public LocalDate getValidUntil() { return validUntil; }
    public void setValidUntil(LocalDate validUntil) { this.validUntil = validUntil; }
    public CinemaManager getCreatedByManager() { return createdByManager; }
    public void setCreatedByManager(CinemaManager createdByManager) { this.createdByManager = createdByManager; }
    public BoxOfficeStaff getCreatedByStaff() { return createdByStaff; }
    public void setCreatedByStaff(BoxOfficeStaff createdByStaff) { this.createdByStaff = createdByStaff; }
}