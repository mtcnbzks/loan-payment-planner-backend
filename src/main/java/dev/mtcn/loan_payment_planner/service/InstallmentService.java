package dev.mtcn.loan_payment_planner.service;

import dev.mtcn.loan_payment_planner.entity.Installment;
import dev.mtcn.loan_payment_planner.entity.Loan;
import dev.mtcn.loan_payment_planner.entity.PeriodFrequencyType;
import dev.mtcn.loan_payment_planner.repository.InstallmentRepository;
import dev.mtcn.loan_payment_planner.repository.LoanRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class InstallmentService {
    private final InstallmentRepository installmentRepository;
    private final LoanRepository loanRepository;

    public InstallmentService(InstallmentRepository installmentRepository, LoanRepository loanRepository) {
        this.installmentRepository = installmentRepository;
        this.loanRepository = loanRepository;
    }

    public List<Installment> createInstallmentPlanFromLoan(Long loanId) {
        Loan loanForInstallmentPlan = loanRepository.findById(loanId).orElseThrow(() -> new RuntimeException("Loan not found"));
        if (installmentRepository.existsByLoanId(loanId)) {
            throw new IllegalArgumentException("Installment plan already exists for the loan.");
        }

        List<Installment> installmentPlansForLoan = new ArrayList<>();

        Integer installmentCount = loanForInstallmentPlan.getInstallmentCount();
        BigDecimal interestPercentage = loanForInstallmentPlan.getInterestRate();
        BigDecimal kkdfRate = loanForInstallmentPlan.getKkdfRate();
        BigDecimal bsmvRate = loanForInstallmentPlan.getBsmvRate();
        BigDecimal amount = loanForInstallmentPlan.getAmount();
        Short periodFrequency = loanForInstallmentPlan.getPeriodFrequency();
        PeriodFrequencyType periodFrequencyType = loanForInstallmentPlan.getPeriodFrequencyType();

        // Calculate the fixed installment amount using the amortization formula
        List<LocalDateTime> paymentDates = generatePaymentDates(loanForInstallmentPlan.getFirstInstallmentDate(),
                installmentCount, periodFrequency, periodFrequencyType);

        BigDecimal fixedInstallmentAmount = calculateEquatedMonthlyInstallment(amount, interestPercentage,
                installmentCount, kkdfRate, bsmvRate, periodFrequencyType);
        BigDecimal remainingCapital = amount;
        for (int installmentNumber = 0; installmentNumber < installmentCount; installmentNumber++) {
            Installment installment = new Installment();
            installment.setLoan(loanForInstallmentPlan);
            installment.setLineNo(installmentNumber + 1);
            installment.setPaymentDate(paymentDates.get(installmentNumber).atZone(ZoneId.systemDefault()).toInstant());
            BigDecimal interestRate = interestPercentage.divide(new BigDecimal(100), 4, RoundingMode.HALF_UP);
            BigDecimal interestAmount = new BigDecimal("0.0");

            switch (periodFrequencyType) {
                case MONTH -> interestAmount = remainingCapital.multiply(interestRate);
                case YEAR -> interestAmount = remainingCapital.multiply(interestRate.multiply(new BigDecimal("12.0")));
            }

            BigDecimal kkdfAmount = interestAmount
                    .multiply(kkdfRate.divide(new BigDecimal(100), 4, RoundingMode.HALF_UP));
            BigDecimal bsmvAmount = interestAmount
                    .multiply(bsmvRate.divide(new BigDecimal(100), 4, RoundingMode.HALF_UP));

            BigDecimal capitalPayment = fixedInstallmentAmount.subtract(interestAmount).subtract(kkdfAmount).subtract(bsmvAmount);
            remainingCapital = remainingCapital.subtract(capitalPayment);

            installment.setCapitalPayment(capitalPayment);
            installment.setInterestAmount(interestAmount);
            installment.setKkdfAmount(kkdfAmount);
            installment.setBsmvAmount(bsmvAmount);
            installment.setRemainingCapital(remainingCapital);
            installment.setInstallmentAmount(fixedInstallmentAmount);

            installmentPlansForLoan.add(installment);
        }

        return installmentPlansForLoan;
    }

    private static BigDecimal calculateEquatedMonthlyInstallment(
            BigDecimal loanAmount,
            BigDecimal monthlyInterestRate,
            Integer installmentCount,
            BigDecimal kkdfRate,
            BigDecimal bsmvRate,
            PeriodFrequencyType periodFrequencyType) {

        // Return simple division if interest rate is 0 (no interest).
        if (monthlyInterestRate.compareTo(BigDecimal.ZERO) == 0) {
            return loanAmount.divide(new BigDecimal(installmentCount), MathContext.DECIMAL128);
        }

        // Determine the interest rate based on the frequency (monthly or yearly).
        BigDecimal interestRate = calculateInterestRate(monthlyInterestRate, periodFrequencyType);

        // Calculate the total interest rate including KKDF and BSMV taxes.
        BigDecimal effectiveInterestRate = calculateEffectiveInterestRate(interestRate, kkdfRate, bsmvRate);

        // EMI formula: EMI = [P * r * (1 + r)^n] / [(1 + r)^n - 1]
        BigDecimal onePlusRate = effectiveInterestRate.add(BigDecimal.ONE);
        BigDecimal onePlusRateToThePowerOfN = onePlusRate.pow(installmentCount, MathContext.DECIMAL128);
        BigDecimal numerator = loanAmount.multiply(effectiveInterestRate).multiply(onePlusRateToThePowerOfN);
        BigDecimal denominator = onePlusRateToThePowerOfN.subtract(BigDecimal.ONE);

        return numerator.divide(denominator, 4, RoundingMode.HALF_UP);
    }

    // Method to calculate interest rate based on the period frequency (monthly or yearly).
    private static BigDecimal calculateInterestRate(BigDecimal monthlyInterestRate, PeriodFrequencyType periodFrequencyType) {
        BigDecimal interestRate = monthlyInterestRate.divide(new BigDecimal(100), 4, RoundingMode.HALF_UP);
        return switch (periodFrequencyType) {
            case MONTH -> interestRate;  // Use monthly rate as is
            case YEAR -> interestRate.multiply(new BigDecimal(12));  // Convert to yearly rate
        };
    }

    // Method to calculate the effective interest rate with taxes (KKDF and BSMV).
    private static BigDecimal calculateEffectiveInterestRate(BigDecimal interestRate, BigDecimal kkdfRate, BigDecimal bsmvRate) {
        BigDecimal kkdfMultiplier = kkdfRate.divide(new BigDecimal(100), 4, RoundingMode.HALF_UP);
        BigDecimal bsmvMultiplier = bsmvRate.divide(new BigDecimal(100), 4, RoundingMode.HALF_UP);
        return interestRate.multiply(BigDecimal.ONE.add(kkdfMultiplier).add(bsmvMultiplier));
    }

    private List<LocalDateTime> generatePaymentDates(Instant firstInstallmentDate, Integer installmentCount, Short periodFrequency, PeriodFrequencyType periodFrequencyType) {
        List<LocalDateTime> paymentDates = new ArrayList<>();

        LocalDateTime nextDate = LocalDateTime.ofInstant(firstInstallmentDate, ZoneId.systemDefault());
        paymentDates.add(nextDate);

        for (int i = 1; i < installmentCount; i++) {
            nextDate = switch (periodFrequencyType) {
                case MONTH -> nextDate.plusMonths(periodFrequency);
                case YEAR -> nextDate.plusYears(periodFrequency);
            };
            paymentDates.add(nextDate);
        }

        return paymentDates;
    }

}
