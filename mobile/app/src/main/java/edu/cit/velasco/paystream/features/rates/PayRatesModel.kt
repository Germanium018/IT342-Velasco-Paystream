package edu.cit.velasco.paystream.features.rates

import java.math.BigDecimal

data class PayRatesRequest(
    val position: String? = null, // 🟢 Added this so we can identify Driver vs Helper
    val baseRate: BigDecimal,
    val rate40ft: BigDecimal,
    val rate20ft: BigDecimal,
    val rateOtContainer: BigDecimal,
    val rateOtHour: BigDecimal,
    val rateSss: BigDecimal,
    val ratePhilhealth: BigDecimal,
    val ratePagibig: BigDecimal
)