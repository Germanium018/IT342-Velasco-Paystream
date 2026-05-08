package edu.cit.velasco.paystream.features.rates;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PayRatesRepository extends JpaRepository<PayRates, String> {
    // Position (e.g., "DRIVER") is the ID
}