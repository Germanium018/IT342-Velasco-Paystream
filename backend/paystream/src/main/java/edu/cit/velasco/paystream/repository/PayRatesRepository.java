package edu.cit.velasco.paystream.repository;

import edu.cit.velasco.paystream.entity.PayRates;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayRatesRepository extends JpaRepository<PayRates, String> {
    // Position (e.g., "DRIVER") is the ID
}