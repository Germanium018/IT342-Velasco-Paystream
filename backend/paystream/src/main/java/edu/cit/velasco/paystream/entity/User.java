package edu.cit.velasco.paystream.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    private String firstname;
    private String lastname;
    private String role; // "ROLE_ADMIN" or "ROLE_EMPLOYEE"

    private LocalDateTime createdAt = LocalDateTime.now();
}