package edu.cit.velasco.paystream.service;

import edu.cit.velasco.paystream.entity.Employee;
import edu.cit.velasco.paystream.entity.PayRates;
import edu.cit.velasco.paystream.entity.PayrollTransaction;
import edu.cit.velasco.paystream.repository.EmployeeRepository;
import edu.cit.velasco.paystream.repository.PayRatesRepository;
import edu.cit.velasco.paystream.repository.PayrollTransactionRepository;
import edu.cit.velasco.paystream.strategy.PayrollCalculationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PayrollService {

    private final EmployeeRepository employeeRepository;
    private final PayRatesRepository payRatesRepository;
    private final PayrollTransactionRepository payrollRepository;

    // This Map automatically holds your strategies! 
    // Key = The name in @Component ("DRIVER_STRATEGY", "HELPER_STRATEGY")
    private final Map<String, PayrollCalculationStrategy> strategies;

    public PayrollTransaction processPayroll(PayrollTransaction request) {
        // 1. Fetch Employee and their specific Position Rates
        Employee emp = employeeRepository.findById(request.getEmployee().getId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        PayRates rates = payRatesRepository.findById(emp.getPosition())
                .orElseThrow(() -> new RuntimeException("Rates not set for position: " + emp.getPosition()));

        // 2. Select the correct Strategy based on the Employee's Position
        // If the position is "Driver", it looks for "DRIVER_STRATEGY"
        String strategyKey = emp.getPosition().toUpperCase() + "_STRATEGY";
        
        PayrollCalculationStrategy strategy = strategies.get(strategyKey);
        
        if (strategy == null) {
            throw new RuntimeException("No payroll strategy found for position: " + emp.getPosition());
        }

        // 3. Delegate the math to the Strategy
        BigDecimal grossPay = strategy.calculateGrossPay(request, emp, rates);
        BigDecimal totalDeductions = strategy.calculateTotalDeductions(request, emp);
        BigDecimal netPay = grossPay.subtract(totalDeductions);

        // 4. Finalize Transaction Data
        request.setEmployee(emp);
        request.setNetPay(netPay);
        request.setTransactionStatus("PAID");

        return payrollRepository.save(request);
    }

    public List<PayrollTransaction> getEmployeeHistory(Long employeeId) {
        return payrollRepository.findByEmployeeId(employeeId);
    }
}