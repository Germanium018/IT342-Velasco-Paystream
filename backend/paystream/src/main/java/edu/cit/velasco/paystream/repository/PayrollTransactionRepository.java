package edu.cit.velasco.paystream.repository;

import edu.cit.velasco.paystream.entity.PayrollTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PayrollTransactionRepository extends JpaRepository<PayrollTransaction, Long> {
    List<PayrollTransaction> findByEmployeeId(Long employeeId);
}