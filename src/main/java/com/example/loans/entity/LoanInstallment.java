package com.example.loans.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "LOAN_INSTALLMENTS")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoanInstallment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long loanId;

    private BigDecimal amount;

    private BigDecimal paidAmount;

    private LocalDate dueDate;

    private LocalDate paymentDate;

    private Boolean isPaid;
    
}
