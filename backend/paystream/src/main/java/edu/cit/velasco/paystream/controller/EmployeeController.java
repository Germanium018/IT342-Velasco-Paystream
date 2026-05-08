package edu.cit.velasco.paystream.controller;

import edu.cit.velasco.paystream.entity.Employee;
import edu.cit.velasco.paystream.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional; // Added for safe profile retrieval

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    /**
     * Fetches all employee records.
     * Primarily used by the Admin Dashboard for management and overview.
     */
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeRepository.findAll());
    }

    /**
     * NEW: Fetches the specific employee profile linked to a User account.
     * Essential for the Employee Dashboard to display personal debt and history.
     */
    @GetMapping("/me/{userId}")
    public ResponseEntity<?> getMyProfile(@PathVariable Long userId) {
        Optional<Employee> employee = employeeRepository.findByUserId(userId);
        
        if (employee.isPresent()) {
            return ResponseEntity.ok(employee.get());
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Registers a new employee profile in the system.
     */
    @PostMapping
    public ResponseEntity<?> addEmployee(@RequestBody Employee employee) {
        return ResponseEntity.ok(employeeRepository.save(employee));
    }

    /**
     * Updates an existing employee's professional details and financial status.
     * Includes logic to persist updated debt balances after payroll processing.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id, @RequestBody Employee details) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        emp.setBaseSalary(details.getBaseSalary());
        emp.setPosition(details.getPosition());
        emp.setStatus(details.getStatus());
        
        // Updates the outstanding balance in the database
        emp.setDebt(details.getDebt()); 
        
        return ResponseEntity.ok(employeeRepository.save(emp));
    }
}