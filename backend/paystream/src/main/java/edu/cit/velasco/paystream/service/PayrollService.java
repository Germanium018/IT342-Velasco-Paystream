package edu.cit.velasco.paystream.service;

import edu.cit.velasco.paystream.entity.Employee;
import edu.cit.velasco.paystream.entity.PayRates;
import edu.cit.velasco.paystream.entity.PayrollTransaction;
import edu.cit.velasco.paystream.repository.EmployeeRepository;
import edu.cit.velasco.paystream.repository.PayRatesRepository;
import edu.cit.velasco.paystream.repository.PayrollTransactionRepository;
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
     * and updates the employee's outstanding debt balance.
     */
    @Transactional
    public PayrollTransaction processPayroll(PayrollTransaction request) {
        // 1. Fetch Employee
        Employee emp = employeeRepository.findById(request.getEmployee().getId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        PayRates rates = payRatesRepository.findById(emp.getPosition())
                .orElseThrow(() -> new RuntimeException("Rates not set for position: " + emp.getPosition()));

        // --- CALCULATION LOGIC ---
        // Uses the Daily Rate from the PayRates table instead of the monthly salary
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

        BigDecimal netPay = grossPay.subtract(totalDeductions);

        // --- DEBT UPDATE LOGIC ---
        if (emp.getDebt() != null && request.getOtherDebts().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal remainingDebt = emp.getDebt().subtract(request.getOtherDebts());
            emp.setDebt(remainingDebt);
            employeeRepository.save(emp); 
        }

        // 2. Finalize Transaction Data
        request.setEmployee(emp);
        request.setNetPay(netPay);
        request.setTransactionStatus("PAID");

        return payrollRepository.save(request);
    }

    /**
     * Fetches all payroll transactions for a specific employee,
     * sorted so the most recent payslips appear at the top.
     */
    public List<PayrollTransaction> getEmployeeHistory(Long employeeId) {
        // Updated to use the sorted repository method
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