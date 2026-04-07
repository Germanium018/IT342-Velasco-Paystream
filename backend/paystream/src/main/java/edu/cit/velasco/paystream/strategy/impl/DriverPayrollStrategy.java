package edu.cit.velasco.paystream.strategy.impl;

// 1. IMPORT YOUR ENTITIES (This tells Java where to find your data models)
import edu.cit.velasco.paystream.entity.Employee;
import edu.cit.velasco.paystream.entity.PayRates;
import edu.cit.velasco.paystream.entity.PayrollTransaction;

// 2. IMPORT THE STRATEGY INTERFACE
import edu.cit.velasco.paystream.strategy.PayrollCalculationStrategy;

// 3. IMPORT JAVA MATH & SPRING TOOLS
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component("DRIVER_STRATEGY")
public class DriverPayrollStrategy implements PayrollCalculationStrategy {

    @Override
    public BigDecimal calculateGrossPay(PayrollTransaction request, Employee emp, PayRates rates) {
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

        return basicPay
                .add(pay40ft)
                .add(pay20ft)
                .add(payOtHours)
                .add(payOtContainers)
                .add(request.getOutOfTownTrips());
    }

    @Override
    public BigDecimal calculateTotalDeductions(PayrollTransaction request, Employee emp) {
        // Absence Deduction: (Absence Days * Daily Rate)
        // Note: Using BaseSalary as daily rate as per your original code
        BigDecimal absenceDeduction = request.getAbsences().multiply(emp.getBaseSalary());

        return request.getSssDeduction()
                .add(request.getPhilhealthDeduction())
                .add(request.getPagibigDeduction())
                .add(absenceDeduction)
                .add(request.getCashAdvance())
                .add(request.getOtherDebts());
    }
}