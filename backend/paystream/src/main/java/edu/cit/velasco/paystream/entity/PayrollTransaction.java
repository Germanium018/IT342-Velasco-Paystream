package edu.cit.velasco.paystream.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @Column(name = "transaction_status")
    private String transactionStatus; // "PENDING", "PAID"

    private LocalDateTime processedAt = LocalDateTime.now();
}