package com.example.loans.repository;

import com.example.loans.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    String CUSTOMERID = "customerId";

    /**
     * Find loans by customer id
     *
     * @param customerId the customer id
     * @return the list of loans created by the given customer
     */
    @Query("FROM Loan loan"
        + " WHERE loan.customerId = :" + CUSTOMERID
    )
    List<Loan> findLoansByCustomerId(
        @Param(CUSTOMERID) long customerId);
}
