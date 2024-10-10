package dev.mtcn.loan_payment_planner.controller;

import dev.mtcn.loan_payment_planner.entity.Installment;
import dev.mtcn.loan_payment_planner.repository.InstallmentRepository;
import dev.mtcn.loan_payment_planner.service.InstallmentService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/installments")
public class InstallmentController {
    private final InstallmentRepository installmentRepository;
    private final InstallmentService installmentService;

    public InstallmentController(InstallmentRepository installmentRepository, InstallmentService installmentService) {
        this.installmentRepository = installmentRepository;
        this.installmentService = installmentService;
    }

    @GetMapping("/{loanId}")
    public ResponseEntity<List<Installment>> findAllByLoanId(@PathVariable Long loanId, Pageable pageable) {
        List<Installment> installments = installmentRepository.findAllByLoanId(loanId);
        return ResponseEntity.ok(installments);
    }

    @PostMapping("/{loanId}")
    ResponseEntity<Void> createInstallment(@PathVariable Long loanId, UriComponentsBuilder uriComponentsBuilder) {
        List<Installment> installmentPlans = installmentService.createInstallmentPlanFromLoan(loanId);
        installmentRepository.saveAll(installmentPlans);

        return ResponseEntity.created(uriComponentsBuilder.path("/api/installments/{id}").buildAndExpand(loanId).toUri()).build();
    }

    @DeleteMapping("/{loanId}")
    ResponseEntity<Void> deleteById(@PathVariable Long loanId) {
        installmentRepository.deleteAll(installmentRepository.findAllByLoanId(loanId));
        return ResponseEntity.noContent().build();
    }

}
