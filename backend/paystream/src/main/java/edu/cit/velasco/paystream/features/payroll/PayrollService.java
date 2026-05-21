package edu.cit.velasco.paystream.features.payroll;

import edu.cit.velasco.paystream.features.employee.Employee;
import edu.cit.velasco.paystream.features.employee.EmployeeRepository;
import edu.cit.velasco.paystream.features.rates.PayRates;
import edu.cit.velasco.paystream.features.rates.PayRatesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PayrollService {

    private final EmployeeRepository employeeRepository;
    private final PayRatesRepository payRatesRepository;
    private final PayrollTransactionRepository payrollRepository;

    /**
     * Processes a payroll transaction, calculates net pay, 
     * locks historical snapshots, and updates the employee's outstanding debt balance.
     */
    @Transactional
    public PayrollTransaction processPayroll(PayrollTransaction request) {
        // 1. Fetch Employee and Rates
        Employee emp = employeeRepository.findById(request.getEmployee().getId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        PayRates rates = payRatesRepository.findById(emp.getPosition())
                .orElseThrow(() -> new RuntimeException("Rates not set for position: " + emp.getPosition()));

        // --- CALCULATION LOGIC ---
        BigDecimal basicPay = request.getWorkingDays().multiply(rates.getBaseRate());
        BigDecimal pay40ft = request.getCount40ft().multiply(rates.getRate40ft());
        BigDecimal pay20ft = request.getCount20ft().multiply(rates.getRate20ft());
        BigDecimal payOtHours = request.getOvertimeHours().multiply(rates.getRateOtHour());
        BigDecimal payOtContainers = request.getOtContainerCount().multiply(rates.getRateOtContainer());

        BigDecimal grossPay = basicPay.add(pay40ft).add(pay20ft).add(payOtHours)
                .add(payOtContainers).add(request.getOutOfTownTrips());

        BigDecimal absenceDeduction = request.getAbsences().multiply(rates.getBaseRate());
        BigDecimal totalDeductions = request.getSssDeduction()
                .add(request.getPhilhealthDeduction())
                .add(request.getPagibigDeduction())
                .add(absenceDeduction)
                .add(request.getCashAdvance())
                .add(request.getOtherDebts());

        // Calculates Net Pay (Can be negative!)
        BigDecimal netPay = grossPay.subtract(totalDeductions);

        // --- UPGRADED DEBT UPDATE LOGIC (Shortfall Rollover) ---
        BigDecimal currentDebt = emp.getDebt() != null ? emp.getDebt() : BigDecimal.ZERO;
        
        // Step A: Subtract any normal "Other Debts" repayment the admin entered on this payslip
        if (request.getOtherDebts().compareTo(BigDecimal.ZERO) > 0) {
            currentDebt = currentDebt.subtract(request.getOtherDebts());
        }

        // Step B: If the payslip is negative, add the shortfall to their outstanding debt
        if (netPay.compareTo(BigDecimal.ZERO) < 0) {
            BigDecimal shortfall = netPay.abs(); // Converts -700 into a positive 700
            currentDebt = currentDebt.add(shortfall); 
        }

        // Save the final calculated debt back to the employee profile
        emp.setDebt(currentDebt);
        employeeRepository.save(emp); 

        // 2. Finalize Transaction Data
        request.setEmployee(emp);
        request.setNetPay(netPay);
        request.setTransactionStatus("PAID");

        // --- Save the Rate & Position Snapshots permanently ---
        request.setPositionAtTime(emp.getPosition());
        request.setRateBase(rates.getBaseRate());
        request.setRate40ft(rates.getRate40ft());
        request.setRate20ft(rates.getRate20ft());
        request.setRateOtHour(rates.getRateOtHour());
        request.setRateOtContainer(rates.getRateOtContainer());

        // --- Save the Calculated Earnings permanently ---
        request.setPayBase(basicPay);
        request.setPay40ft(pay40ft);
        request.setPay20ft(pay20ft);
        request.setPayOtHour(payOtHours);
        request.setPayOtContainer(payOtContainers);
        request.setGrossPay(grossPay);
        request.setAbsenceDeductionAmount(absenceDeduction);

        return payrollRepository.save(request);
    }

    /**
     * Fetches all payroll transactions for a specific employee,
     * sorted so the most recent payslips appear at the top.
     */
    public List<PayrollTransaction> getEmployeeHistory(Long employeeId) {
        return payrollRepository.findByEmployeeIdOrderByProcessedAtDesc(employeeId);
    }

    /**
     * Fetches the master list of all payroll transactions for all employees,
     * sorted so the most recent payslips appear at the top.
     */
    public List<PayrollTransaction> getAllTransactions() {
        return payrollRepository.findAllByOrderByProcessedAtDesc();
    }
}