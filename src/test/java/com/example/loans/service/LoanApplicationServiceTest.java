package com.example.loans.service;

import com.example.loans.dto.LoanCreationDTO;
import com.example.loans.dto.LoanPaymentDTO;
import com.example.loans.dto.LoanPaymentResultDTO;
import com.example.loans.entity.Customer;
import com.example.loans.entity.Loan;
import com.example.loans.entity.LoanInstallment;
import com.example.loans.repository.CustomerRepository;
import com.example.loans.repository.LoanInstallmentRepository;
import com.example.loans.repository.LoanRepository;
import com.example.loans.response.CreateLoanResponse;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.event.annotation.BeforeTestClass;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class LoanApplicationServiceTest {

    @InjectMocks
    private LoanApplicationService loanApplicationService;

    @Mock
    private CustomerRepository customerRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private LoanInstallmentRepository loanInstallmentRepository;

    @Mock
    private Customer customer;

    @BeforeEach
    public void setUp(){
        loanApplicationService = new LoanApplicationService(customerRepository, loanRepository, loanInstallmentRepository);
    }

    @Test
    public void testCreateLoanWithCustomerDoesNotExists(){
        LoanCreationDTO loanCreationDTO = new LoanCreationDTO();
        loanCreationDTO.setCustomerId(2L);
        loanCreationDTO.setAmount(new BigDecimal(10000));
        loanCreationDTO.setInterestRate(new BigDecimal("0.5"));
        loanCreationDTO.setNumberOfInstallments(6);

        Long customerId = 1L;
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setCreditLimit(new BigDecimal(100000));
        customer.setName("Orhan");
        customer.setSurName("Senturk");
        customer.setUsedCreditLimit(new BigDecimal(0));
        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        ResponseEntity<CreateLoanResponse> response = loanApplicationService.createLoan(loanCreationDTO);

        assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("No proper customer found"));
    }

    @Test
    public void testCreateLoanWithCustomerDoesNotHaveEnoughCreditLimit(){
        LoanCreationDTO loanCreationDTO = new LoanCreationDTO();
        loanCreationDTO.setCustomerId(1L);
        loanCreationDTO.setAmount(new BigDecimal(1000000));
        loanCreationDTO.setInterestRate(new BigDecimal("0.5"));
        loanCreationDTO.setNumberOfInstallments(6);

        Long customerId = 1L;
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setCreditLimit(new BigDecimal(100000));
        customer.setName("Orhan");
        customer.setSurName("Senturk");
        customer.setUsedCreditLimit(new BigDecimal(0));
        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        ResponseEntity<CreateLoanResponse> response = loanApplicationService.createLoan(loanCreationDTO);

        assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Customer credit limit is not enough"));
    }

    @Test
    public void testCreateLoanWithWrongInstallmentType(){
        LoanCreationDTO loanCreationDTO = new LoanCreationDTO();
        loanCreationDTO.setCustomerId(1L);
        loanCreationDTO.setAmount(new BigDecimal(10000));
        loanCreationDTO.setInterestRate(new BigDecimal("0.5"));
        loanCreationDTO.setNumberOfInstallments(7);

        Long customerId = 1L;
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setCreditLimit(new BigDecimal(100000));
        customer.setName("Orhan");
        customer.setSurName("Senturk");
        customer.setUsedCreditLimit(new BigDecimal(0));
        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        ResponseEntity<CreateLoanResponse> response = loanApplicationService.createLoan(loanCreationDTO);

        assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("InstallmentType can only be"));
    }

    @Test
    public void testCreateLoanWithIncorrectInterestRate(){
        LoanCreationDTO loanCreationDTO = new LoanCreationDTO();
        loanCreationDTO.setCustomerId(1L);
        loanCreationDTO.setAmount(new BigDecimal(10000));
        loanCreationDTO.setInterestRate(new BigDecimal("0.6"));
        loanCreationDTO.setNumberOfInstallments(6);

        Long customerId = 1L;
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setCreditLimit(new BigDecimal(100000));
        customer.setName("Orhan");
        customer.setSurName("Senturk");
        customer.setUsedCreditLimit(new BigDecimal(0));
        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        ResponseEntity<CreateLoanResponse> response = loanApplicationService.createLoan(loanCreationDTO);

        assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Interest rate should be between 0.1 and 0.5"));
    }

    @Test
    public void testCreateLoan(){
        LoanCreationDTO loanCreationDTO = new LoanCreationDTO();
        loanCreationDTO.setCustomerId(1L);
        loanCreationDTO.setAmount(new BigDecimal(10000));
        loanCreationDTO.setInterestRate(new BigDecimal("0.5"));
        loanCreationDTO.setNumberOfInstallments(6);

        Long customerId = 1L;
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setCreditLimit(new BigDecimal(100000));
        customer.setName("Orhan");
        customer.setSurName("Senturk");
        customer.setUsedCreditLimit(new BigDecimal(0));
        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        ResponseEntity<CreateLoanResponse> response = loanApplicationService.createLoan(loanCreationDTO);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Loan created successfully"));

        assertEquals(response.getBody().getLoan().getLoanAmount(),
            BigDecimal.ONE.add(loanCreationDTO.getInterestRate()).multiply(loanCreationDTO.getAmount()) );
    }

    @Test
    public void testCreateLoanLoanInstallmentsCreated(){
        LoanCreationDTO loanCreationDTO = new LoanCreationDTO();
        loanCreationDTO.setCustomerId(1L);
        loanCreationDTO.setAmount(new BigDecimal(10000));
        loanCreationDTO.setInterestRate(new BigDecimal("0.5"));
        loanCreationDTO.setNumberOfInstallments(6);

        Long customerId = 1L;
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setCreditLimit(new BigDecimal(100000));
        customer.setName("Orhan");
        customer.setSurName("Senturk");
        customer.setUsedCreditLimit(new BigDecimal(0));
        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        ResponseEntity<CreateLoanResponse> response = loanApplicationService.createLoan(loanCreationDTO);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Loan created successfully"));

        List<LoanInstallment> loanInstallments =
            loanInstallmentRepository.findInstallmentsByLoanId(response.getBody().getLoan().getId());

        assertNotNull(loanInstallments);
    }

    @Test
    public void testPayLoanWithCompleteSpent(){
        LoanCreationDTO loanCreationDTO = new LoanCreationDTO();
        loanCreationDTO.setCustomerId(1L);
        loanCreationDTO.setAmount(new BigDecimal(10000));
        loanCreationDTO.setInterestRate(new BigDecimal("0.5"));
        loanCreationDTO.setNumberOfInstallments(6);

        Long customerId = 1L;
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setCreditLimit(new BigDecimal(100000));
        customer.setName("Orhan");
        customer.setSurName("Senturk");
        customer.setUsedCreditLimit(new BigDecimal(0));
        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        ResponseEntity<CreateLoanResponse> response = loanApplicationService.createLoan(loanCreationDTO);

        LoanPaymentDTO loanPaymentDTO = new LoanPaymentDTO();
        loanPaymentDTO.setLoanId(response.getBody().getLoan().getId());
        loanPaymentDTO.setPaymentAmount(new BigDecimal(5000));
        ResponseEntity<LoanPaymentResultDTO> paymentResult = loanApplicationService.payLoan(loanPaymentDTO);

        assertNotNull(paymentResult.getBody());
        assertEquals(2, paymentResult.getBody().getNumberOfInstallmentsPaid());
        assertThat(new BigDecimal(5000),  Matchers.comparesEqualTo(paymentResult.getBody().getTotalAmountSpent()));
    }

    @Test
    public void testPayLoanWithRemainingAmount(){
        LoanCreationDTO loanCreationDTO = new LoanCreationDTO();
        loanCreationDTO.setCustomerId(1L);
        loanCreationDTO.setAmount(new BigDecimal(10000));
        loanCreationDTO.setInterestRate(new BigDecimal("0.5"));
        loanCreationDTO.setNumberOfInstallments(6);

        Long customerId = 1L;
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setCreditLimit(new BigDecimal(100000));
        customer.setName("Orhan");
        customer.setSurName("Senturk");
        customer.setUsedCreditLimit(new BigDecimal(0));
        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        ResponseEntity<CreateLoanResponse> response = loanApplicationService.createLoan(loanCreationDTO);

        LoanPaymentDTO loanPaymentDTO = new LoanPaymentDTO();
        loanPaymentDTO.setLoanId(response.getBody().getLoan().getId());
        loanPaymentDTO.setPaymentAmount(new BigDecimal(7000));
        ResponseEntity<LoanPaymentResultDTO> paymentResult = loanApplicationService.payLoan(loanPaymentDTO);

        assertNotNull(paymentResult.getBody());
        assertEquals(2, paymentResult.getBody().getNumberOfInstallmentsPaid());
        assertThat(new BigDecimal(5000),  Matchers.comparesEqualTo(paymentResult.getBody().getTotalAmountSpent()));
    }


    @Test
    public void testPayLoanWithMoreThan3Months(){
        LoanCreationDTO loanCreationDTO = new LoanCreationDTO();
        loanCreationDTO.setCustomerId(1L);
        loanCreationDTO.setAmount(new BigDecimal(10000));
        loanCreationDTO.setInterestRate(new BigDecimal("0.5"));
        loanCreationDTO.setNumberOfInstallments(6);

        Long customerId = 1L;
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setCreditLimit(new BigDecimal(100000));
        customer.setName("Orhan");
        customer.setSurName("Senturk");
        customer.setUsedCreditLimit(new BigDecimal(0));
        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        ResponseEntity<CreateLoanResponse> response = loanApplicationService.createLoan(loanCreationDTO);

        LoanPaymentDTO loanPaymentDTO = new LoanPaymentDTO();
        loanPaymentDTO.setLoanId(response.getBody().getLoan().getId());
        loanPaymentDTO.setPaymentAmount(new BigDecimal(8000));
        ResponseEntity<LoanPaymentResultDTO> paymentResult = loanApplicationService.payLoan(loanPaymentDTO);

        assertNotNull(paymentResult.getBody());
        assertEquals(2, paymentResult.getBody().getNumberOfInstallmentsPaid());
        assertThat(new BigDecimal(5000),  Matchers.comparesEqualTo(paymentResult.getBody().getTotalAmountSpent()));
    }

    @Test
    public void testListLoansByCustomerId(){
        LoanCreationDTO loanCreationDTO = new LoanCreationDTO();
        loanCreationDTO.setCustomerId(1L);
        loanCreationDTO.setAmount(new BigDecimal(10000));
        loanCreationDTO.setInterestRate(new BigDecimal("0.5"));
        loanCreationDTO.setNumberOfInstallments(6);

        Long customerId = 1L;
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setCreditLimit(new BigDecimal(100000));
        customer.setName("Orhan");
        customer.setSurName("Senturk");
        customer.setUsedCreditLimit(new BigDecimal(0));
        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        loanApplicationService.createLoan(loanCreationDTO);

        List<Loan> loans = loanRepository.findLoansByCustomerId(loanCreationDTO.getCustomerId());

        assertNotNull(loans);
        assertEquals(1, loans.size());
    }

    @Test
    public void testListLoanInstallmentsByLoanId(){
        LoanCreationDTO loanCreationDTO = new LoanCreationDTO();
        loanCreationDTO.setCustomerId(1L);
        loanCreationDTO.setAmount(new BigDecimal(10000));
        loanCreationDTO.setInterestRate(new BigDecimal("0.5"));
        loanCreationDTO.setNumberOfInstallments(6);

        Long customerId = 1L;
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setCreditLimit(new BigDecimal(100000));
        customer.setName("Orhan");
        customer.setSurName("Senturk");
        customer.setUsedCreditLimit(new BigDecimal(0));
        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        ResponseEntity<CreateLoanResponse> response = loanApplicationService.createLoan(loanCreationDTO);

        assertNotNull(response.getBody());
        List<LoanInstallment>  loanInstallments =
            loanInstallmentRepository.findInstallmentsByLoanId(response.getBody().getLoan().getId());

        assertNotNull(loanInstallments);
        assertEquals(6, loanInstallments.size());
    }

    @Test
    public void testListUnpaidLoanInstallmentsByLoanId(){
        LoanCreationDTO loanCreationDTO = new LoanCreationDTO();
        loanCreationDTO.setCustomerId(1L);
        loanCreationDTO.setAmount(new BigDecimal(10000));
        loanCreationDTO.setInterestRate(new BigDecimal("0.5"));
        loanCreationDTO.setNumberOfInstallments(6);

        Long customerId = 1L;
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setCreditLimit(new BigDecimal(100000));
        customer.setName("Orhan");
        customer.setSurName("Senturk");
        customer.setUsedCreditLimit(new BigDecimal(0));
        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        ResponseEntity<CreateLoanResponse> response = loanApplicationService.createLoan(loanCreationDTO);

        assertNotNull(response.getBody());

        LoanPaymentDTO loanPaymentDTO = new LoanPaymentDTO();
        loanPaymentDTO.setLoanId(response.getBody().getLoan().getId());
        loanPaymentDTO.setPaymentAmount(new BigDecimal(5000));
        loanApplicationService.payLoan(loanPaymentDTO);

        List<LoanInstallment> loanInstallments =
            loanInstallmentRepository.findUnpaidInstallmentsByLoanId(response.getBody().getLoan().getId());

        assertNotNull(loanInstallments);
        assertEquals(4, loanInstallments.size());
    }
}