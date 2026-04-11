package edu.cit.velasco.paystream.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "pay_rates")
@Data
public class PayRates {
    @Id
    private String position; // "DRIVER" or "HELPER"

    // Container & OT Rates
    @Column(name = "base_rate", precision = 15, scale = 2)
    private BigDecimal baseRate;

    @Column(name = "rate_40ft", precision = 15, scale = 2)
    private BigDecimal rate40ft;

    @Column(name = "rate_20ft", precision = 15, scale = 2)
    private BigDecimal rate20ft;

    @Column(name = "rate_ot_container", precision = 15, scale = 2)
    private BigDecimal rateOtContainer;

    @Column(name = "rate_ot_hour", precision = 15, scale = 2)
    private BigDecimal rateOtHour;

    // Statutory Rates (New Fields)
    @Column(name = "rate_sss", precision = 15, scale = 2)
    private BigDecimal rateSss;

    @Column(name = "rate_philhealth", precision = 15, scale = 2)
    private BigDecimal ratePhilhealth;

    @Column(name = "rate_pagibig", precision = 15, scale = 2)
    private BigDecimal ratePagibig;
}