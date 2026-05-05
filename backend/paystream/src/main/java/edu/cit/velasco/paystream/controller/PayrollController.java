package edu.cit.velasco.paystream.controller;

import edu.cit.velasco.paystream.entity.PayrollTransaction;
import edu.cit.velasco.paystream.service.PayrollService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payroll")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class PayrollController {

    private final PayrollService payrollService;

    @PostMapping("/process")
    public ResponseEntity<?> process(@RequestBody PayrollTransaction request) {
        try {
            PayrollTransaction result = payrollService.processPayroll(request);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "netPay", result.getNetPay(),
                "status", result.getTransactionStatus()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // Existing: Get history for a specific employee
    @GetMapping("/history/{employeeId}")
    public ResponseEntity<List<PayrollTransaction>> getHistory(@PathVariable Long employeeId) {
        return ResponseEntity.ok(payrollService.getEmployeeHistory(employeeId));
    }

    /**
     * STEP 1 FIX: New Endpoint for Phase 2
     * Fetches all transactions for all employees, sorted by latest first.
     */
    @GetMapping("/all")
    public ResponseEntity<List<PayrollTransaction>> getAllHistory() {
        return ResponseEntity.ok(payrollService.getAllTransactions());
    }
}