package edu.cit.velasco.paystream.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "payslip_files")
@Data
public class PayslipFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "payroll_id")
    private PayrollTransaction payrollTransaction;

    private String filePath;
    
    @Column(name = "file_type")
    private String fileType = "application/pdf";

    @Column(name = "generated_at")
    private LocalDateTime generatedAt = LocalDateTime.now();
}