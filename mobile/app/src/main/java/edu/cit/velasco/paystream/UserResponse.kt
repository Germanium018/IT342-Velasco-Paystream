package edu.cit.velasco.paystream

data class UserResponse(
    val id: Long,
    val email: String,
    val firstname: String,
    val lastname: String,
    val role: String
)