package dev.mtcn.loan_payment_planner.controller;

import dev.mtcn.loan_payment_planner.entity.Loan;
import dev.mtcn.loan_payment_planner.repository.LoanRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/loans")
public class LoanController {
    private final LoanRepository loanRepository;

    public LoanController(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @GetMapping("/{loanId}")
    private ResponseEntity<Loan> findById(@PathVariable Long loanId) {
        Optional<Loan> optionalLoan = loanRepository.findById(loanId);
        return optionalLoan.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    private ResponseEntity<List<Loan>> findAll(Pageable pageable) {
        Page<Loan> page = loanRepository.findAll(
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize()
                        // pageable.getSortOr(Sort.by(Sort.Direction.ASC, "created_at"))
                ));
        return ResponseEntity.ok(page.getContent());
    }

    @PostMapping
    private ResponseEntity<Void> createLoan(@RequestBody Loan newLoan, UriComponentsBuilder uriComponentsBuilder) {
        Loan savedLoan = loanRepository.save(newLoan);
        return ResponseEntity.created(uriComponentsBuilder.path("/api/loans/{id}").buildAndExpand(savedLoan.getId()).toUri()).build();
    }

    @PutMapping("/{loanId}")
    private ResponseEntity<Loan> updateLoan(@PathVariable Long loanId, @RequestBody Loan updatedLoan) {
        Optional<Loan> optionalLoan = loanRepository.findById(loanId);

        if (optionalLoan.isPresent()) {
            updatedLoan.setId(loanId);
            loanRepository.save(updatedLoan);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{requestedId}")
    private ResponseEntity<Void> deleteLoan(@PathVariable Long requestedId) {
        Optional<Loan> optionalLoan = loanRepository.findById(requestedId);

        if (optionalLoan.isPresent()) {
            loanRepository.deleteById(requestedId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
