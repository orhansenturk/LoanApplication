package com.example.loans.repository;

import com.example.loans.entity.LoanInstallment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanInstallmentRepository extends JpaRepository<LoanInstallment, Long> {

    String LOANID = "loanId";

    /**
     * Find loan installments by loan id
     *
     * @param loanId the loan id
     * @return the list of loan installments created fot the given loan
     */
    @Query("FROM LoanInstallment li"
        + " WHERE li.loanId = :" + LOANID
        + " AND li.isPaid <> true"
        + " ORDER BY li.dueDate"
    )
    List<LoanInstallment> findUnpaidInstallmentsByLoanId(
        @Param(LOANID) long loanId);

    /**
     * Find loan installments by loan id
     *
     * @param loanId the loan id
     * @return the list of loan installments created fot the given loan
     */
    @Query("FROM LoanInstallment li"
        + " WHERE li.loanId = :" + LOANID
        + " ORDER BY li.dueDate"
    )
    List<LoanInstallment> findInstallmentsByLoanId(
        @Param(LOANID) long loanId);
}
