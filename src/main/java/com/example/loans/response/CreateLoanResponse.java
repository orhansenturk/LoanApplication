package com.example.loans.response;

import com.example.loans.entity.Loan;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateLoanResponse {
    private Loan loan;
    private String message;
}
