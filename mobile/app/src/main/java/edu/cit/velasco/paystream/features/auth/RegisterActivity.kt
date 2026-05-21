package edu.cit.velasco.paystream.features.auth

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import edu.cit.velasco.paystream.R
import edu.cit.velasco.paystream.core.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Links this Kotlin file to your existing XML layout
        setContentView(R.layout.activity_register)

        // Adjust padding for system bars (Resolves R.id.main error)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Get references to UI elements
        val etFirstName = findViewById<TextInputEditText>(R.id.etFirstName)
        val etLastName = findViewById<TextInputEditText>(R.id.etLastName)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnSignUp = findViewById<MaterialButton>(R.id.btnSignUp)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val tvLoginLink = findViewById<TextView>(R.id.tvLoginLink)

        // 2. Navigation: Back to Login screen
        btnBack.setOnClickListener { finish() }
        tvLoginLink.setOnClickListener { finish() }

        // 3. Logic to handle Sign Up button click
        btnSignUp.setOnClickListener {
            val firstName = etFirstName.text.toString().trim()
            val lastName = etLastName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Basic validation
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create request object (Role is ROLE_EMPLOYEE by default)
            val registerRequest = RegisterRequest(firstName, lastName, email, password)

            // Send request to Spring Boot via Retrofit
            RetrofitClient.instance.registerUser(registerRequest).enqueue(object : Callback<AuthResponse> {
                override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(this@RegisterActivity, "Registration Successful!", Toast.LENGTH_LONG).show()
                        finish() // Closes this screen and goes back to Login
                    } else {
                        // Extract error message from backend (e.g., "Email already exists")
                        val errorMsg = response.body()?.message ?: "Registration Failed"
                        Toast.makeText(this@RegisterActivity, errorMsg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    Toast.makeText(this@RegisterActivity, "Connection Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}