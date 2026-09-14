package com.cinema.movie_reservation_system.controller;

import com.cinema.movie_reservation_system.model.Voucher;
import com.cinema.movie_reservation_system.service.VoucherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vouchers")
public class VoucherController {

    private final VoucherService voucherService;

    public VoucherController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @GetMapping
    public List<Voucher> listAll() {
        return voucherService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Voucher> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(voucherService.getById(id));
    }

    @PostMapping
    public ResponseEntity<Voucher> create(@RequestBody Map<String, Object> body) {
        BigDecimal discountRate = new BigDecimal(body.get("discountRate").toString());
        LocalDate validFrom = LocalDate.parse(body.get("validFrom").toString());
        LocalDate validUntil = LocalDate.parse(body.get("validUntil").toString());
        Long managerId = body.containsKey("createdByManagerId")
                ? Long.valueOf(body.get("createdByManagerId").toString()) : null;
        String code = (String) body.get("voucherCode");
        return ResponseEntity.ok(voucherService.createVoucher(discountRate, validFrom, validUntil, managerId, code));
    }

    @GetMapping("/validate/{code}")
    public ResponseEntity<Voucher> validate(@PathVariable String code) {
        return ResponseEntity.ok(voucherService.validateCode(code));
    }

    @PutMapping("/{id}/use")
    public ResponseEntity<Voucher> markUsed(@PathVariable Long id) {
        return ResponseEntity.ok(voucherService.markUsed(id));
    }
}