package edu.cit.velasco.paystream

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    // Matches your AuthController @PostMapping("/register")
    @POST("api/v1/auth/register")
    fun registerUser(@Body request: RegisterRequest): Call<AuthResponse>

    // Matches your AuthController @PostMapping("/login")
    @POST("api/v1/auth/login")
    fun loginUser(@Body request: LoginRequest): Call<AuthResponse>
}