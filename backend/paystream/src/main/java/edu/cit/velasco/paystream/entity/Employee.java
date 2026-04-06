package edu.cit.velasco.paystream.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "employees")
@Data
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "base_salary", precision = 15, scale = 2)
    private BigDecimal baseSalary;

    private String position; // "DRIVER" or "HELPER"
    private String status;   // "ACTIVE", "INACTIVE"
    
    @Column(name = "hire_date")
    private LocalDate hireDate;
}