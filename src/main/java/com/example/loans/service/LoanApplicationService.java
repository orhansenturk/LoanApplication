package com.example.loans.service;

import com.example.loans.dto.LoanCreationDTO;
import com.example.loans.dto.LoanPaymentDTO;
import com.example.loans.dto.LoanPaymentResultDTO;
import com.example.loans.entity.Customer;
import com.example.loans.entity.Loan;
import com.example.loans.entity.LoanInstallment;
import com.example.loans.enums.NumberOfInstallments;
import com.example.loans.repository.CustomerRepository;
import com.example.loans.repository.LoanInstallmentRepository;
import com.example.loans.repository.LoanRepository;
import com.example.loans.response.CreateLoanResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class LoanApplicationService {

    private final CustomerRepository customerRepository;
    private final LoanRepository loanRepository;
    private final LoanInstallmentRepository loanInstallmentRepository;

    @Autowired
    public LoanApplicationService(CustomerRepository customerRepository,
                                  LoanRepository loanRepository,LoanInstallmentRepository loanInstallmentRepository){
        this.customerRepository = customerRepository;
        this.loanRepository = loanRepository;
        this.loanInstallmentRepository = loanInstallmentRepository;
    }

    public ResponseEntity<CreateLoanResponse> createLoan(LoanCreationDTO loanCreationDTO) {
        CreateLoanResponse response = new CreateLoanResponse();
        if (loanCreationDTO == null) {
            response.setMessage("Request object is null");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Customer customer = findCustomer(loanCreationDTO.getCustomerId());
        if (customer == null) {
            response.setMessage("No proper customer found with customerId:" + loanCreationDTO.getCustomerId());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (customer.getCreditLimit().compareTo(loanCreationDTO.getAmount()) < 0) {

            response.setMessage("Customer credit limit is not enough, credit limit is: " + customer.getCreditLimit());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (!correctInstallmentType(loanCreationDTO.getNumberOfInstallments())) {
            response.setMessage("InstallmentType can only be: "
                + Arrays.stream(NumberOfInstallments.values()).map(NumberOfInstallments::getNumberOfInstallments).toList()
                + "but your request has: " + loanCreationDTO.getNumberOfInstallments());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (!correctInterestRate(loanCreationDTO.getInterestRate())) {
            response.setMessage("Interest rate should be between 0.1 and 0.5 but yours is " + loanCreationDTO.getInterestRate());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Loan loan = new Loan();
        loan.setCustomerId(loanCreationDTO.getCustomerId());
        loan.setNumberOfInstallment(loanCreationDTO.getNumberOfInstallments());
        loan.setLoanAmount(calculateLoanAmount(loanCreationDTO.getAmount(), loanCreationDTO.getInterestRate()));
        loan.setIsPaid(Boolean.FALSE);
        loan.setCreateDate(new Date());

        loan = loanRepository.save(loan);

        createLoanInstallments(loan, loanCreationDTO.getNumberOfInstallments());

        customer.setCreditLimit(customer.getCreditLimit().subtract(loanCreationDTO.getAmount()));
        customer.setUsedCreditLimit(loanCreationDTO.getAmount());
        customerRepository.save(customer);

        response.setMessage("Loan created successfully");
        response.setLoan(loan);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private void createLoanInstallments(Loan loan, int numberOfInstallments) {
        BigDecimal installmentAmount = calculateInstallmentAmount(loan.getLoanAmount(), numberOfInstallments);

        for (int i = 0; i < numberOfInstallments; i++) {
            LoanInstallment loanInstallment = new LoanInstallment();
            loanInstallment.setIsPaid(Boolean.FALSE);
            loanInstallment.setLoanId(loan.getId());
            loanInstallment.setAmount(installmentAmount);
            loanInstallment.setPaidAmount(BigDecimal.ZERO);
            loanInstallment.setDueDate(calculateDueDate(LocalDate.now().plusMonths(i)));
            loanInstallment.setPaymentDate(null);
            loanInstallmentRepository.save(loanInstallment);
        }
    }

    private LocalDate calculateDueDate(LocalDate localDate) {
        return localDate.with(TemporalAdjusters.firstDayOfNextMonth());
    }

    private BigDecimal calculateInstallmentAmount(BigDecimal loanAmount, int numberOfInstallments) {
        return loanAmount.divide(new BigDecimal(numberOfInstallments));
    }

    private BigDecimal calculateLoanAmount(BigDecimal amount, BigDecimal interestRate) {
        return amount.multiply(BigDecimal.ONE.add(interestRate));
    }

    private boolean correctInterestRate(BigDecimal interestRate) {
        return interestRate.compareTo(new BigDecimal("0.1")) >= 0 &&
            interestRate.compareTo(new BigDecimal("0.5")) <= 0;
    }

    private Customer findCustomer(Long customerId) {
        return customerRepository.findById(customerId).orElse(null);
    }

    private boolean correctInstallmentType(int value) {
        for (NumberOfInstallments n : NumberOfInstallments.values()) {
            if (n.getNumberOfInstallments() == value) {
                return true;
            }
        }
        return false;
    }

    public ResponseEntity<List<Loan>> getLoansByCustomerId(Long customerId) {

        try {
            List<Loan> loans = loanRepository.findLoansByCustomerId(customerId);

            if (CollectionUtils.isEmpty(loans)) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(loans, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<LoanInstallment>> getInstallmentsByLoanId(Long loanId) {
        try {
            List<LoanInstallment> loanInstallments = loanInstallmentRepository.findInstallmentsByLoanId(loanId);

            if (CollectionUtils.isEmpty(loanInstallments)) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(loanInstallments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<LoanPaymentResultDTO> payLoan(LoanPaymentDTO loanPaymentDTO) {
        List<LoanInstallment> loanInstallments =
            loanInstallmentRepository.findUnpaidInstallmentsByLoanId(loanPaymentDTO.getLoanId());
        if (CollectionUtils.isEmpty(loanInstallments)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        BigDecimal leftAmount = loanPaymentDTO.getPaymentAmount();
        int numberOfInstallmentsPaid = 0;
        for (LoanInstallment loanInstallment : loanInstallments) {
            if (loanInstallment.getAmount().compareTo(leftAmount) > 0) {
                break;
            } else {
                LocalDate currentMonthFirstDay = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
                if (Period.between(currentMonthFirstDay, loanInstallment.getDueDate()).getMonths() <= 2) {
                    payInstallment(loanInstallment);
                    leftAmount = leftAmount.subtract(loanInstallment.getAmount());
                    numberOfInstallmentsPaid++;
                }
            }
        }

        LoanPaymentResultDTO loanPaymentResultDTO = new LoanPaymentResultDTO();
        BigDecimal totalAmountSpent = loanPaymentDTO.getPaymentAmount().subtract(leftAmount);
        loanPaymentResultDTO.setNumberOfInstallmentsPaid(numberOfInstallmentsPaid);
        loanPaymentResultDTO.setTotalAmountSpent(totalAmountSpent);
        boolean isPaidCompletely = loanInstallments.size() == numberOfInstallmentsPaid;
        loanPaymentResultDTO.setPaidCompletely(isPaidCompletely);

        Loan loan = loanRepository.findById(loanPaymentDTO.getLoanId()).orElse(null);
        if (loan != null && isPaidCompletely) {
            loan.setIsPaid(true);
            loanRepository.save(loan);
        }

        if (loan != null) {
            Customer customer = customerRepository.findById(loan.getCustomerId()).orElse(null);
            if (customer != null) {
                customer.setCreditLimit(customer.getCreditLimit().add(totalAmountSpent));
                customerRepository.save(customer);
            }
        }

        return new ResponseEntity<>(loanPaymentResultDTO, HttpStatus.OK);
    }

    private void payInstallment(LoanInstallment loanInstallment) {
        loanInstallment.setPaymentDate(LocalDate.now());
        loanInstallment.setIsPaid(Boolean.TRUE);
        loanInstallment.setPaidAmount(loanInstallment.getAmount());
        loanInstallmentRepository.save(loanInstallment);
    }
}
