package edu.cit.velasco.paystream.controller;

import edu.cit.velasco.paystream.entity.Employee;
import edu.cit.velasco.paystream.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//import java.util.Map;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    // Get all drivers and helpers for the dashboard
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeRepository.findAll());
    }

    // Add a new employee profile
    @PostMapping
    public ResponseEntity<?> addEmployee(@RequestBody Employee employee) {
        return ResponseEntity.ok(employeeRepository.save(employee));
    }

    // Update base salary, position, status, and debt
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id, @RequestBody Employee details) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        emp.setBaseSalary(details.getBaseSalary());
        emp.setPosition(details.getPosition());
        emp.setStatus(details.getStatus());
        
        // THIS IS THE NEW LINE: Allow the backend to save the debt to the database
        emp.setDebt(details.getDebt()); 
        
        return ResponseEntity.ok(employeeRepository.save(emp));
    }
}