package dev.mtcn.loan_payment_planner.repository;

import dev.mtcn.loan_payment_planner.entity.Installment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface InstallmentRepository extends CrudRepository<Installment, Long>, PagingAndSortingRepository<Installment, Long> {
    List<Installment> findAllByLoanId(Long loanId);

    boolean existsByLoanId(Long loanId);
}
