package edu.cit.velasco.paystream

data class AuthResponse(
    val success: Boolean,
    val message: String?, // The '?' means it can be null if login is successful
    val user: UserResponse? // The user object returned on successful login
)