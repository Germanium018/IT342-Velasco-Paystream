package edu.cit.velasco.paystream.controller;

import edu.cit.velasco.paystream.entity.PayRates;
import edu.cit.velasco.paystream.repository.PayRatesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rates")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class PayRatesController {

    private final PayRatesRepository payRatesRepository;

    @GetMapping
    public List<PayRates> getRates() {
        return payRatesRepository.findAll();
    }

    @PutMapping("/{position}")
    public ResponseEntity<PayRates> updateRates(@PathVariable String position, @RequestBody PayRates rates) {
        rates.setPosition(position.toUpperCase());
        return ResponseEntity.ok(payRatesRepository.save(rates));
    }
}