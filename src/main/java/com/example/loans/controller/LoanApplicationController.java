package com.example.loans.controller;

import com.example.loans.dto.LoanCreationDTO;
import com.example.loans.dto.LoanPaymentDTO;
import com.example.loans.dto.LoanPaymentResultDTO;
import com.example.loans.entity.Loan;
import com.example.loans.entity.LoanInstallment;
import com.example.loans.response.CreateLoanResponse;
import com.example.loans.service.LoanApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/loanApplication")
public class LoanApplicationController {

    @Autowired
    private LoanApplicationService loanApplicationService;

    @PostMapping("/createLoan")
    public ResponseEntity<CreateLoanResponse> createLoan(@RequestBody LoanCreationDTO loanCreationDTO){
        return loanApplicationService.createLoan(loanCreationDTO);
    }

    @GetMapping("/listLoansByCustomerId/{customerId}")
    public ResponseEntity<List<Loan>> getLoansByCustomerId(@PathVariable Long customerId){
        return loanApplicationService.getLoansByCustomerId(customerId);
    }

    @GetMapping("/listInstallmentsByLoanId/{loanId}")
    public ResponseEntity<List<LoanInstallment>> getInstallmentsByLoanId(@PathVariable Long loanId){
        return loanApplicationService.getInstallmentsByLoanId(loanId);
    }

    @PostMapping("/payLoan")
    public ResponseEntity<LoanPaymentResultDTO> payLoan(@RequestBody LoanPaymentDTO loanPaymentDTO){
        return loanApplicationService.payLoan(loanPaymentDTO);
    }
}
