package dev.mtcn.loan_payment_planner.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "loan", schema = "sekerbank")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loan_id_gen")
    @SequenceGenerator(name = "loan_id_gen", sequenceName = "loan_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ManyToOne()
    @JoinColumn(name = "loan_group_id", nullable = false)
    private LoanGroup loanGroup;

    @Column(name = "type", nullable = false, length = Integer.MAX_VALUE)
    private String type;

    @Column(name = "installment_count", nullable = false)
    private Integer installmentCount;

    @Lob
    @Enumerated(EnumType.STRING)
    @Column(name = "customer_type", nullable = false)
    private CustomerType customerType;

    @Lob
    @Enumerated(EnumType.STRING)
    @Column(name = "currency_type", nullable = false)
    private CurrencyType currencyType;

    @Column(name = "interest_rate", nullable = false, precision = 38, scale = 2)
    private BigDecimal interestRate;

    @Column(name = "expense", nullable = false, precision = 38, scale = 2)
    private BigDecimal expense;

    @Column(name = "kkdf_rate", nullable = false, precision = 38, scale = 2)
    private BigDecimal kkdfRate;

    @Column(name = "bsmv_rate", nullable = false, precision = 38, scale = 2)
    private BigDecimal bsmvRate;

    @Column(name = "amount", nullable = false, precision = 38, scale = 2)
    private BigDecimal amount;

    @Column(name = "first_installment_date", nullable = false)
    private Instant firstInstallmentDate;

    @Column(name = "period_frequency", nullable = false)
    private Short periodFrequency;

    @Lob
    @Enumerated(EnumType.STRING)
    @Column(name = "period_frequency_type", nullable = false)
    private PeriodFrequencyType periodFrequencyType;

    @Lob
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    private PaymentType paymentType;

    @Column(name = "search_name", length = Integer.MAX_VALUE, unique = true)
    private String searchName;

}