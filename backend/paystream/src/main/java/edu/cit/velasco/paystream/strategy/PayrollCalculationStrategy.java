package edu.cit.velasco.paystream.strategy;

import edu.cit.velasco.paystream.entity.Employee;
import edu.cit.velasco.paystream.entity.PayRates;
import edu.cit.velasco.paystream.entity.PayrollTransaction;
import java.math.BigDecimal;

public interface PayrollCalculationStrategy {
    BigDecimal calculateGrossPay(PayrollTransaction request, Employee emp, PayRates rates);
    BigDecimal calculateTotalDeductions(PayrollTransaction request, Employee emp);
}