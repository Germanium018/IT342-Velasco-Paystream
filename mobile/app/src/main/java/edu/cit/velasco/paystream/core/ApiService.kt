package edu.cit.velasco.paystream.core

import edu.cit.velasco.paystream.features.auth.AuthResponse
import edu.cit.velasco.paystream.features.auth.LoginRequest
import edu.cit.velasco.paystream.features.auth.RegisterRequest
import edu.cit.velasco.paystream.features.employee.EmployeeResponse
import edu.cit.velasco.paystream.features.rates.PayRatesRequest
import edu.cit.velasco.paystream.features.payroll.PayrollTransactionRequest
import edu.cit.velasco.paystream.features.payroll.PayrollResponse
import edu.cit.velasco.paystream.features.payroll.PayrollTransactionResponse
import edu.cit.velasco.paystream.features.employee.EmployeeUpdateRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.*

interface ApiService {

    // Matches your AuthController @PostMapping("/register")
    @POST("api/v1/auth/register")
    fun registerUser(@Body request: RegisterRequest): Call<AuthResponse>

    // Matches your AuthController @PostMapping("/login")
    @POST("api/v1/auth/login")
    fun loginUser(@Body request: LoginRequest): Call<AuthResponse>

    @GET("api/v1/employees")
    fun getEmployees(
        @Header("Authorization") token: String
    ): Call<List<EmployeeResponse>>

    @GET("api/v1/rates")
    fun getRates(
        @Header("Authorization") token: String
    ): Call<List<PayRatesRequest>>

    @PUT("api/v1/rates/{position}")
    fun updateRates(
        @Header("Authorization") token: String,
        @Path("position") position: String,
        @Body rates: PayRatesRequest
    ): Call<PayRatesRequest>

    @POST("api/v1/payroll/process")
    fun processPayroll(
        @Header("Authorization") token: String,
        @Body request: PayrollTransactionRequest
    ): Call<PayrollResponse>

    // Add this right below your processPayroll endpoint in ApiService.kt
    @GET("api/v1/payroll/all")
    fun getAllPayslips(
        @Header("Authorization") token: String
    ): Call<List<PayrollTransactionResponse>>

    @PUT("api/v1/employees/{id}")
    fun updateEmployee(
        @Header("Authorization") token: String,
        @Path("id") employeeId: Long,
        @Body request: EmployeeUpdateRequest
    ): Call<EmployeeResponse>

    // 🟢 Gets the specific employee profile (to check their Outstanding Debt)
    @GET("api/v1/employees/me/{id}")
    fun getEmployeeProfile(
        @Header("Authorization") token: String,
        @Path("id") employeeId: Long
    ): Call<EmployeeResponse>

    // 🟢 Gets the personal payroll history for this specific employee
    @GET("api/v1/payroll/history/{id}")
    fun getPersonalPayslips(
        @Header("Authorization") token: String,
        @Path("id") employeeId: Long
    ): Call<List<PayrollTransactionResponse>>
}