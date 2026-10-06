package com.cinema.movie_reservation_system.service;

import com.cinema.movie_reservation_system.model.Voucher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class VoucherServiceTest {

    @Autowired
    private VoucherService voucherService;

    @Test
    void testInitialVouchersLoaded() {
        List<Voucher> vouchers = voucherService.getAllVouchers();
        assertNotNull(vouchers);
        assertFalse(vouchers.isEmpty());
        assertTrue(vouchers.stream().anyMatch(v -> "CINE200".equals(v.getCode())));
    }

    @Test
    void testValidateActiveVoucherSuccess() {
        Voucher voucher = voucherService.validateVoucherCode("CINE200");
        assertNotNull(voucher);
        assertEquals(200.0, voucher.getDiscountAmountLkr());
        assertTrue(voucher.isActive());
    }

    @Test
    void testValidateInactiveVoucherThrows() {
        // EXPIRED50 is seeded with is_active = false
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> voucherService.validateVoucherCode("EXPIRED50"));
        assertTrue(ex.getMessage().contains("inactive") || ex.getMessage().contains("expired"));
    }

    @Test
    void testValidateNonExistentVoucherThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> voucherService.validateVoucherCode("NONEXISTENT_CODE"));
    }

    @Test
    void testCreateAndToggleVoucher() {
        Voucher v = new Voucher("PROMO300", 300.0, true);
        Voucher created = voucherService.createVoucher(v);
        assertNotNull(created.getId());
        assertEquals("PROMO300", created.getCode());

        Voucher toggled = voucherService.toggleVoucherStatus(created.getId());
        assertFalse(toggled.isActive());
    }

    @Test
    void testDuplicateVoucherCodeThrows() {
        Voucher v1 = new Voucher("DUPCODE", 100.0, true);
        voucherService.createVoucher(v1);

        Voucher v2 = new Voucher("DUPCODE", 200.0, true);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> voucherService.createVoucher(v2));
        assertTrue(ex.getMessage().contains("already exists"));
    }
}
