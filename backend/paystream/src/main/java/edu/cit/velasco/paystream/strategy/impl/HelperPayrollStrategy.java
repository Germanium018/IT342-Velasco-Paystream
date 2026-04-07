package edu.cit.velasco.paystream.strategy.impl;

import edu.cit.velasco.paystream.entity.Employee;
import edu.cit.velasco.paystream.entity.PayRates;
import edu.cit.velasco.paystream.entity.PayrollTransaction;
import edu.cit.velasco.paystream.strategy.PayrollCalculationStrategy;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component("HELPER_STRATEGY")
public class HelperPayrollStrategy implements PayrollCalculationStrategy {

    @Override
    public BigDecimal calculateGrossPay(PayrollTransaction request, Employee emp, PayRates rates) {
        // Helpers usually only get Basic Pay + OT Hours + Out of Town Allowance
        // They typically do NOT get 40ft/20ft container bonuses
        
        BigDecimal basicPay = request.getWorkingDays().multiply(emp.getBaseSalary());
        
        BigDecimal payOtHours = request.getOvertimeHours().multiply(rates.getRateOtHour());

        return basicPay
                .add(payOtHours)
                .add(request.getOutOfTownTrips());
    }

    @Override
    public BigDecimal calculateTotalDeductions(PayrollTransaction request, Employee emp) {
        // Deductions usually remain standard across roles
        BigDecimal absenceDeduction = request.getAbsences().multiply(emp.getBaseSalary());

        return request.getSssDeduction()
                .add(request.getPhilhealthDeduction())
                .add(request.getPagibigDeduction())
                .add(absenceDeduction)
                .add(request.getCashAdvance())
                .add(request.getOtherDebts());
    }
}