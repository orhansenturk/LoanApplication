package com.example.loans.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class LoanPaymentResultDTO {

    private int numberOfInstallmentsPaid;
    private BigDecimal totalAmountSpent;
    private boolean isPaidCompletely;
}
