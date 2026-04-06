package edu.cit.velasco.paystream.service;

import edu.cit.velasco.paystream.entity.Employee;
import edu.cit.velasco.paystream.entity.PayRates;
import edu.cit.velasco.paystream.entity.PayrollTransaction;
import edu.cit.velasco.paystream.repository.EmployeeRepository;
import edu.cit.velasco.paystream.repository.PayRatesRepository;
import edu.cit.velasco.paystream.repository.PayrollTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PayrollService {

    private final EmployeeRepository employeeRepository;
    private final PayRatesRepository payRatesRepository;
    private final PayrollTransactionRepository payrollRepository;

    public PayrollTransaction processPayroll(PayrollTransaction request) {
        // 1. Fetch Employee and their specific Position Rates
        Employee emp = employeeRepository.findById(request.getEmployee().getId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        PayRates rates = payRatesRepository.findById(emp.getPosition())
                .orElseThrow(() -> new RuntimeException("Rates not set for position: " + emp.getPosition()));

        // --- GROSS PAY CALCULATION ---
        // (Working Days * Base Salary)
        BigDecimal basicPay = request.getWorkingDays().multiply(emp.getBaseSalary());
        
        // (40ft count * 40ft rate)
        BigDecimal pay40ft = request.getCount40ft().multiply(rates.getRate40ft());
        
        // (20ft count * 20ft rate)
        BigDecimal pay20ft = request.getCount20ft().multiply(rates.getRate20ft());
        
        // (OT hours * OT rate)
        BigDecimal payOtHours = request.getOvertimeHours().multiply(rates.getRateOtHour());
        
        // (OT container count * OT container rate)
        BigDecimal payOtContainers = request.getOtContainerCount().multiply(rates.getRateOtContainer());

        BigDecimal grossPay = basicPay
                .add(pay40ft)
                .add(pay20ft)
                .add(payOtHours)
                .add(payOtContainers)
                .add(request.getOutOfTownTrips());

        // --- DEDUCTIONS CALCULATION ---
        // Absence Deduction: (Absence Days * Daily Rate)
        BigDecimal absenceDeduction = request.getAbsences().multiply(emp.getBaseSalary());

        BigDecimal totalDeductions = request.getSssDeduction()
                .add(request.getPhilhealthDeduction())
                .add(request.getPagibigDeduction())
                .add(absenceDeduction)
                .add(request.getCashAdvance())
                .add(request.getOtherDebts());

        // --- NET PAY ---
        BigDecimal netPay = grossPay.subtract(totalDeductions);

        // 2. Finalize Transaction Data
        request.setEmployee(emp);
        request.setNetPay(netPay);
        request.setTransactionStatus("PAID");

        return payrollRepository.save(request);
    }

    public List<PayrollTransaction> getEmployeeHistory(Long employeeId) {
        return payrollRepository.findByEmployeeId(employeeId);
    }
}