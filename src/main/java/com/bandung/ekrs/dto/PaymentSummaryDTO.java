package com.bandung.ekrs.dto;

import com.bandung.ekrs.model.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSummaryDTO {
    private Long totalTransactions;
    private BigDecimal totalAmount;
    private PaymentStatus paymentStatus;
} 