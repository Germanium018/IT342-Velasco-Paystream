package edu.cit.velasco.paystream.repository;

import edu.cit.velasco.paystream.entity.PayrollTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PayrollTransactionRepository extends JpaRepository<PayrollTransaction, Long> {
    
    // FOR EMPLOYEE DASHBOARD: Get personal history, newest first
    List<PayrollTransaction> findByEmployeeIdOrderByProcessedAtDesc(Long employeeId);
    
    // FOR ADMIN VIEW: Get all records, newest first
    List<PayrollTransaction> findAllByOrderByProcessedAtDesc();
}