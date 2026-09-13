package com.cinema.movie_reservation_system.service;

import com.cinema.movie_reservation_system.model.RefundRequest;
import com.cinema.movie_reservation_system.repository.RefundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class RefundService {

    @Autowired
    private RefundRepository refundRepository;

    public RefundRequest processCancellation(Long bookingId, Long customerId, BigDecimal originalAmount, String reason) {
        BigDecimal feeRate = new BigDecimal("0.10");
        BigDecimal cancellationFee = originalAmount.multiply(feeRate);
        BigDecimal refundAmount = originalAmount.subtract(cancellationFee);

        RefundRequest request = new RefundRequest();
        request.setBookingId(bookingId);
        request.setCustomerId(customerId);
        request.setRequestType("CANCELLATION");
        request.setOriginalAmount(originalAmount);
        request.setCancellationFee(cancellationFee);
        request.setRefundAmount(refundAmount);
        request.setStatus("APPROVED");
        request.setReason(reason);

        return refundRepository.save(request);
    }

    public List<RefundRequest> getAllRefunds() {
        return refundRepository.findAll();
    }
}