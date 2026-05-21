package edu.cit.velasco.paystream.features.payroll

import java.math.BigDecimal
import edu.cit.velasco.paystream.features.employee.EmployeeResponse

data class EmployeeIdWrapper(
    val id: Long
)

data class PayrollTransactionRequest(
    val employee: EmployeeIdWrapper,
    val monthYear: String,
    val workingDays: BigDecimal,
    val count40ft: BigDecimal,
    val count20ft: BigDecimal,
    val overtimeHours: BigDecimal,
    val otContainerCount: BigDecimal,
    val outOfTownTrips: BigDecimal,
    val absences: BigDecimal,
    val sssDeduction: BigDecimal,
    val philhealthDeduction: BigDecimal,
    val pagibigDeduction: BigDecimal,
    val cashAdvance: BigDecimal,
    val otherDebts: BigDecimal,
    val netPay: BigDecimal
)

data class PayrollResponse(
    val success: Boolean,
    val netPay: BigDecimal?,
    val status: String?,
    val message: String?
)

data class PayrollTransactionResponse(
    val id: Long,
    val employee: EmployeeResponse,
    val monthYear: String,
    val netPay: BigDecimal,
    val processedAt: String?,

    // Deductions
    val sssDeduction: BigDecimal,
    val philhealthDeduction: BigDecimal,
    val pagibigDeduction: BigDecimal,
    val absences: BigDecimal,
    val cashAdvance: BigDecimal,
    val otherDebts: BigDecimal,

    // Counts
    val workingDays: BigDecimal,
    val count40ft: BigDecimal,
    val count20ft: BigDecimal,
    val overtimeHours: BigDecimal,
    val otContainerCount: BigDecimal,
    val outOfTownTrips: BigDecimal,

    // Rate Snapshots (Locked)
    val rateBase: BigDecimal?,
    val rate40ft: BigDecimal?,
    val rate20ft: BigDecimal?,
    val rateOtHour: BigDecimal?,
    val rateOtContainer: BigDecimal?,

    // Calculated Earnings (Locked)
    val payBase: BigDecimal?,
    val pay40ft: BigDecimal?,
    val pay20ft: BigDecimal?,
    val payOtHour: BigDecimal?,
    val payOtContainer: BigDecimal?,
    val grossPay: BigDecimal?,
    val absenceDeductionAmount: BigDecimal?,

    // Position Snapshot (Locked)
    val positionAtTime: String?
)