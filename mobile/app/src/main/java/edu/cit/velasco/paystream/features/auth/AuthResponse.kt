package edu.cit.velasco.paystream.features.auth

data class AuthResponse(
    val success: Boolean,
    val message: String?,
    val user: UserResponse?,
    val token: String? // 🟢 Added this line so Android knows how to grab the JWT!
)