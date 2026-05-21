package edu.cit.velasco.paystream.features.auth

data class LoginRequest(
    val email: String,
    val password: String
)