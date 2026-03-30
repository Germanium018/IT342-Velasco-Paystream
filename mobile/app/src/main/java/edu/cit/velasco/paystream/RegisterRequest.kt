package edu.cit.velasco.paystream

data class RegisterRequest(
    val firstname: String,
    val lastname: String,
    val email: String,
    val password: String,
    val role: String = "ROLE_EMPLOYEE" // Defaulted to Employee as discussed
)