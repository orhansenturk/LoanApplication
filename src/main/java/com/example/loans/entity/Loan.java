package com.example.loans.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "LOANS")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long customerId;

    private BigDecimal loanAmount;

    private Integer numberOfInstallment;

    private Date createDate;

    private Boolean isPaid;

}
