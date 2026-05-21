package edu.cit.velasco.paystream.features.payroll

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.cit.velasco.paystream.R
import edu.cit.velasco.paystream.core.HeaderManager
import edu.cit.velasco.paystream.core.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class PayslipsActivity : AppCompatActivity() {

    private lateinit var rvPayslips: RecyclerView
    private lateinit var adapter: PayslipAdapter
    private var masterList: List<PayrollTransactionResponse> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payslips)

        HeaderManager.setupHeader(this, "Payslips")

        rvPayslips = findViewById(R.id.rvPayslips)
        rvPayslips.layoutManager = LinearLayoutManager(this)

        fetchPayslipsFromBackend()
    }

    private fun fetchPayslipsFromBackend() {
        // 🟢 THE FIX: Swap the placeholder for the real local storage token
        val token = getSharedPreferences("PayStreamPrefs", Context.MODE_PRIVATE).getString("JWT_TOKEN", null)

        if (token == null) {
            Toast.makeText(this, "Auth error. Please log in again.", Toast.LENGTH_SHORT).show()
            return
        }

        val realToken = "Bearer $token"

        RetrofitClient.instance.getAllPayslips(realToken).enqueue(object : Callback<List<PayrollTransactionResponse>> {
            override fun onResponse(call: Call<List<PayrollTransactionResponse>>, response: Response<List<PayrollTransactionResponse>>) {
                if (response.isSuccessful && response.body() != null) {
                    masterList = response.body()!!
                    setupRecyclerView(masterList)
                } else {
                    Toast.makeText(this@PayslipsActivity, "Failed to load payslips", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<PayrollTransactionResponse>>, t: Throwable) {
                Toast.makeText(this@PayslipsActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupRecyclerView(list: List<PayrollTransactionResponse>) {
        adapter = PayslipAdapter(list,
            onViewClick = { payslip ->
                // Keeps the Quick Summary modal for the eye icon
                showPayslipDetailsModal(payslip)
            },
            onDownloadClick = { payslip ->
                // 🟢 THE FIX: Trigger the Native Android PDF Generator
                Toast.makeText(this, "Generating PDF...", Toast.LENGTH_SHORT).show()
                PdfGeneratorTool.generateAndSavePayslip(this, payslip)
            }
        )
        rvPayslips.adapter = adapter
    }

    // Replace your existing showPayslipDetailsModal function with this:

    private fun showPayslipDetailsModal(payslip: PayrollTransactionResponse) {
        val df = java.text.DecimalFormat("#,##0.00")
        val firstName = payslip.employee.user.firstname ?: "Unknown"
        val lastName = payslip.employee.user.lastname ?: ""

        // Safely extract locked data
        val rBase = payslip.rateBase ?: java.math.BigDecimal.ZERO
        val pBase = payslip.payBase ?: java.math.BigDecimal.ZERO
        val absAmount = payslip.absenceDeductionAmount ?: java.math.BigDecimal.ZERO

        val message = """
        Base: Php ${df.format(pBase)}
        40ft Container: Php ${df.format(payslip.pay40ft ?: java.math.BigDecimal.ZERO)}
        20ft Container: Php ${df.format(payslip.pay20ft ?: java.math.BigDecimal.ZERO)}
        Overtime Hours: Php ${df.format(payslip.payOtHour ?: java.math.BigDecimal.ZERO)}
        Overtime Container: Php ${df.format(payslip.payOtContainer ?: java.math.BigDecimal.ZERO)}
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

        // 🟢 THE FIX: Extract snapshot title for modal title bar
        val jobTitle = (payslip.positionAtTime ?: payslip.employee.position).uppercase()

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("$firstName $lastName - $jobTitle")
            .setMessage(message)
            .setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}