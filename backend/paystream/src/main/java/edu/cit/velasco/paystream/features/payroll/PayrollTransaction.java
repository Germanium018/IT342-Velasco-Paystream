package edu.cit.velasco.paystream.features.payroll;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import edu.cit.velasco.paystream.features.employee.Employee;

@Entity
@Table(name = "payroll_transactions")
@Data
public class PayrollTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "month_year")
    private String monthYear;

    @Column(name = "position_at_time", length = 50)
    private String positionAtTime;

    @Column(name = "working_days", precision = 15, scale = 2)
    private BigDecimal workingDays;

    @Column(name = "count_40ft", precision = 15, scale = 2)
    private BigDecimal count40ft;

    @Column(name = "count_20ft", precision = 15, scale = 2)
    private BigDecimal count20ft;

    @Column(name = "overtime_hours", precision = 15, scale = 2)
    private BigDecimal overtimeHours;

    @Column(name = "ot_container_count", precision = 15, scale = 2)
    private BigDecimal otContainerCount;

    @Column(name = "out_of_town_trips", precision = 15, scale = 2)
    private BigDecimal outOfTownTrips;

    @Column(precision = 15, scale = 2)
    private BigDecimal absences;

    // Manual Deductions
    @Column(name = "sss_deduction", precision = 15, scale = 2)
    private BigDecimal sssDeduction;

    @Column(name = "philhealth_deduction", precision = 15, scale = 2)
    private BigDecimal philhealthDeduction;

    @Column(name = "pagibig_deduction", precision = 15, scale = 2)
    private BigDecimal pagibigDeduction;

    @Column(name = "cash_advance", precision = 15, scale = 2)
    private BigDecimal cashAdvance;

    @Column(name = "other_debts", precision = 15, scale = 2)
    private BigDecimal otherDebts;

    @Column(name = "net_pay", precision = 15, scale = 2)
    private BigDecimal netPay;

    @Column(name = "payment_mode")
    private String paymentMode = "CASH";

    // --- NEW: Rate Snapshots (Locks the multiplier forever) ---
    @Column(name = "rate_base", precision = 15, scale = 2)
    private BigDecimal rateBase;

    @Column(name = "rate_40ft", precision = 15, scale = 2)
    private BigDecimal rate40ft;

    @Column(name = "rate_20ft", precision = 15, scale = 2)
    private BigDecimal rate20ft;

    @Column(name = "rate_ot_hour", precision = 15, scale = 2)
    private BigDecimal rateOtHour;

    @Column(name = "rate_ot_container", precision = 15, scale = 2)
    private BigDecimal rateOtContainer;

    // --- NEW: Calculated Earnings (Locks the math forever) ---
    @Column(name = "pay_base", precision = 15, scale = 2)
    private BigDecimal payBase;

    @Column(name = "pay_40ft", precision = 15, scale = 2)
    private BigDecimal pay40ft;

    @Column(name = "pay_20ft", precision = 15, scale = 2)
    private BigDecimal pay20ft;

    @Column(name = "pay_ot_hour", precision = 15, scale = 2)
    private BigDecimal payOtHour;

    @Column(name = "pay_ot_container", precision = 15, scale = 2)
    private BigDecimal payOtContainer;

    @Column(name = "gross_pay", precision = 15, scale = 2)
    private BigDecimal grossPay;
    
    // Locks the actual money deducted for absences, separate from the 'count' of absences
    @Column(name = "absence_deduction_amount", precision = 15, scale = 2)
    private BigDecimal absenceDeductionAmount;

    @Column(name = "transaction_status")
    private String transactionStatus; // "PENDING", "PAID"

    private LocalDateTime processedAt = LocalDateTime.now();
}