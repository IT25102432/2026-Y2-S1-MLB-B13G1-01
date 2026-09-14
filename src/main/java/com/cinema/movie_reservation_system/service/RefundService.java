package com.cinema.movie_reservation_system.service;

import com.cinema.movie_reservation_system.dto.CancelBookingRequest;
import com.cinema.movie_reservation_system.model.RefundRequest;
import com.cinema.movie_reservation_system.repository.RefundRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class RefundService {

    private static final BigDecimal CANCELLATION_FEE_PERCENTAGE = new BigDecimal("0.10");

    private final RefundRepository refundRepository;

    public RefundService(RefundRepository refundRepository) {
        this.refundRepository = refundRepository;
    }

    @Transactional
    public RefundRequest processCancellation(CancelBookingRequest request) {
        BigDecimal originalAmount = request.getOriginalAmount() != null
                ? request.getOriginalAmount()
                : BigDecimal.ZERO;

        BigDecimal cancellationFee = originalAmount.multiply(CANCELLATION_FEE_PERCENTAGE)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal refundAmount = originalAmount.subtract(cancellationFee)
                .setScale(2, RoundingMode.HALF_UP);

        RefundRequest refund = new RefundRequest();
        refund.setBookingId(request.getBookingId());
        refund.setCustomerId(request.getCustomerId());
        refund.setReason(request.getReason());
        refund.setOriginalAmount(originalAmount);
        refund.setCancellationFee(cancellationFee);
        refund.setRefundAmount(refundAmount);
        refund.setRequestType("CANCELLATION");
        refund.setStatus("PROCESSED");

        return refundRepository.save(refund);
    }

    public List<RefundRequest> getAllRefunds() {
        return refundRepository.findAll();
    }
}