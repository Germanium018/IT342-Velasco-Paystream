package edu.cit.velasco.paystream.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    /**
     * BREAK THE LOOP: 
     * When we fetch an Employee, we want the User info, 
     * but we don't want the User to try to load its own "employee" field.
     */
    @JsonIgnoreProperties({"employee", "createdAt"})
    private User user;

    @Column(name = "base_salary", precision = 15, scale = 2)
    private BigDecimal baseSalary;

    private String position; // "DRIVER" or "HELPER"
    private String status;   // "ACTIVE", "INACTIVE"
    
    @Column(name = "hire_date")
    private LocalDate hireDate;
}