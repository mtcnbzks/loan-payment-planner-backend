package dev.mtcn.loan_payment_planner.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "installment", schema = "sekerbank")
public class Installment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "installment_id_gen")
    @SequenceGenerator(name = "installment_id_gen", sequenceName = "installment_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @Column(name = "line_no", nullable = false)
    private Integer lineNo;

    @Column(name = "payment_date", nullable = false)
    private Instant paymentDate;

    @Column(name = "remaining_capital", nullable = false)
    private BigDecimal remainingCapital;

    @Column(name = "capital_payment", nullable = false)
    private BigDecimal capitalPayment;

    @Column(name = "interest_amount", nullable = false)
    private BigDecimal interestAmount;

    @Column(name = "kkdf_amount", nullable = false)
    private BigDecimal kkdfAmount;

    @Column(name = "bsmv_amount", nullable = false)
    private BigDecimal bsmvAmount;

    @Column(name = "installment_amount", nullable = false)
    private BigDecimal installmentAmount;

}