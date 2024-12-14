package com.example.loans.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class LoanCreationDTO {

    private Long customerId;

    private BigDecimal amount;

    private BigDecimal interestRate;

    private int numberOfInstallments;
}
