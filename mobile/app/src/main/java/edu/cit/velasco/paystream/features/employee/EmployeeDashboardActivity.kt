package edu.cit.velasco.paystream.features.employee

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.cit.velasco.paystream.R
import edu.cit.velasco.paystream.core.RetrofitClient
import edu.cit.velasco.paystream.features.payroll.EmployeePayslipAdapter
import edu.cit.velasco.paystream.features.payroll.PayrollTransactionResponse
import edu.cit.velasco.paystream.features.payroll.PdfGeneratorTool
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal
import java.text.DecimalFormat

class EmployeeDashboardActivity : AppCompatActivity() {

    private lateinit var tvOutstandingBalance: TextView
    private lateinit var rvHistory: RecyclerView
    private lateinit var adapter: EmployeePayslipAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_employee_dashboard)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Get saved data
        val prefs = getSharedPreferences("PayStreamPrefs", Context.MODE_PRIVATE)
        val token = "Bearer " + prefs.getString("JWT_TOKEN", "")
        val userId = prefs.getLong("USER_ID", -1L)
        val firstName = prefs.getString("FIRST_NAME", "Employee")

        // Setup UI
        findViewById<TextView>(R.id.tvWelcome).text = "Welcome back, $firstName!"
        tvOutstandingBalance = findViewById(R.id.tvOutstandingBalance)
        rvHistory = findViewById(R.id.rvEmployeeHistory)

        // 🟢 NEW: Hook up the Profile Icon to the Logout Dialog
        val btnProfile = findViewById<android.widget.ImageView>(R.id.btnProfile)
        btnProfile.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        rvHistory.layoutManager = LinearLayoutManager(this)
        adapter = EmployeePayslipAdapter(emptyList(),
            onViewClick = { payslip -> showPayslipDetailsModal(payslip) },
            onDownloadClick = { payslip -> PdfGeneratorTool.generateAndSavePayslip(this, payslip) }
        )
        rvHistory.adapter = adapter

        // Fetch Data
        if (userId != -1L) {
            fetchEmployeeProfile(token, userId)
            fetchPayrollHistory(token, userId)
        }
    }

    private fun fetchEmployeeProfile(token: String, userId: Long) {
        RetrofitClient.instance.getEmployeeProfile(token, userId).enqueue(object : Callback<EmployeeResponse> {
            override fun onResponse(call: Call<EmployeeResponse>, response: Response<EmployeeResponse>) {
                if (response.isSuccessful) {
                    val debt = response.body()?.debt ?: BigDecimal.ZERO
                    val df = DecimalFormat("#,##0.00")
                    tvOutstandingBalance.text = "Php ${df.format(debt)}"
                }
            }
            override fun onFailure(call: Call<EmployeeResponse>, t: Throwable) {
                Toast.makeText(this@EmployeeDashboardActivity, "Failed to load balance", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchPayrollHistory(token: String, userId: Long) {
        RetrofitClient.instance.getPersonalPayslips(token, userId).enqueue(object : Callback<List<PayrollTransactionResponse>> {
            override fun onResponse(call: Call<List<PayrollTransactionResponse>>, response: Response<List<PayrollTransactionResponse>>) {
                if (response.isSuccessful) {
                    val history = response.body() ?: emptyList()
                    adapter.updateData(history)
                }
            }
            override fun onFailure(call: Call<List<PayrollTransactionResponse>>, t: Throwable) {
                Toast.makeText(this@EmployeeDashboardActivity, "Failed to load history", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showPayslipDetailsModal(payslip: PayrollTransactionResponse) {
        val df = DecimalFormat("#,##0.00")
        val firstName = payslip.employee.user.firstname ?: "Unknown"
        val lastName = payslip.employee.user.lastname ?: ""

        val rBase = payslip.rateBase ?: BigDecimal.ZERO
        val pBase = payslip.payBase ?: BigDecimal.ZERO
        val absAmount = payslip.absenceDeductionAmount ?: BigDecimal.ZERO

        val message = """
            Base: Php ${df.format(pBase)}
            40ft Container: Php ${df.format(payslip.pay40ft ?: BigDecimal.ZERO)}
            20ft Container: Php ${df.format(payslip.pay20ft ?: BigDecimal.ZERO)}
            Overtime Hours: Php ${df.format(payslip.payOtHour ?: BigDecimal.ZERO)}
            Overtime Container: Php ${df.format(payslip.payOtContainer ?: BigDecimal.ZERO)}
            Out of Town: Php ${df.format(payslip.outOfTownTrips)}
            
            Absences: - Php ${df.format(absAmount)}
            Cash Advance: - Php ${df.format(payslip.cashAdvance)}
            Other Debts: - Php ${df.format(payslip.otherDebts)}
            SSS: - Php ${df.format(payslip.sssDeduction)}
            PhilHealth: - Php ${df.format(payslip.philhealthDeduction)}
            PagIBIG: - Php ${df.format(payslip.pagibigDeduction)}
            
            ------------------------------------------
            TOTAL AMOUNT: Php ${df.format(payslip.netPay)}
            
            Prepared by: Admin
            Received by: $firstName $lastName
        """.trimIndent()

        val jobTitle = (payslip.positionAtTime ?: payslip.employee.position).uppercase()

        AlertDialog.Builder(this)
            .setTitle("$firstName $lastName - $jobTitle")
            .setMessage(message)
            .setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    // 🟢 NEW: Secure Logout Logic
    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Log Out")
            .setMessage("Are you sure you want to log out of PayStream?")
            .setPositiveButton("Yes") { _, _ ->
                // 1. Erase the saved JWT token and user data
                val prefs = getSharedPreferences("PayStreamPrefs", Context.MODE_PRIVATE)
                prefs.edit().clear().apply()

                // 2. Route to Login AND destroy the Back Stack
                val intent = Intent(this, edu.cit.velasco.paystream.features.auth.LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}