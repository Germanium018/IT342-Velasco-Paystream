package edu.cit.velasco.paystream.features.employee

import java.math.BigDecimal

data class EmployeeResponse(
    val id: Long,
    val user: UserEmbedded,
    val baseSalary: BigDecimal,
    val debt: BigDecimal?,
    val position: String, // "DRIVER" or "HELPER"
    val status: String    // "ACTIVE" or "INACTIVE"
)

// 🟢 Updated to match the actual Spring Boot database fields
data class UserEmbedded(
    val id: Long,
    val firstname: String?,
    val lastname: String?,
    val email: String?
)

data class EmployeeUpdateRequest(
    val debt: BigDecimal,
    val position: String,
    val status: String
)