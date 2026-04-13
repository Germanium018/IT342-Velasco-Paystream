package edu.cit.velasco.paystream.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @JsonIgnore
    // Made nullable=true because GitHub users won't have a local password
    @Column(name = "password_hash", nullable = true) 
    private String passwordHash;

    private String firstname;
    private String lastname;
    private String role; // "ROLE_ADMIN" or "ROLE_EMPLOYEE"
    
    // Track where the user came from
    private String provider; // "LOCAL" or "GITHUB"

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("user")
    private Employee employee;

    private LocalDateTime createdAt = LocalDateTime.now();
}