package dev.mtcn.loan_payment_planner.repository;

import dev.mtcn.loan_payment_planner.entity.Loan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface LoanRepository extends CrudRepository<Loan, Long>, PagingAndSortingRepository<Loan, Long> {
}
