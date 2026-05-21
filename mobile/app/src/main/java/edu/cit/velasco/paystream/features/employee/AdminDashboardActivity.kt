package edu.cit.velasco.paystream.features.employee

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.cit.velasco.paystream.R
import edu.cit.velasco.paystream.core.HeaderManager
import edu.cit.velasco.paystream.core.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EmployeeAdapter
    private lateinit var etSearch: EditText

    private var completeList: List<EmployeeResponse> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        HeaderManager.setupHeader(this, "Dashboard")

        etSearch = findViewById(R.id.etSearch)
        recyclerView = findViewById(R.id.rvEmployees)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = EmployeeAdapter(listOf()) { selectedEmployee ->
            val firstName = selectedEmployee.user.firstname ?: "Unknown"
            val lastName = selectedEmployee.user.lastname ?: ""
            showEditEmployeeModal(selectedEmployee)
        }
        recyclerView.adapter = adapter

        setupSearchFilter()
        fetchEmployeeDirectory()
    }

    private fun fetchEmployeeDirectory() {
        // 🟢 RETRIEVE THE REAL TOKEN FROM LOCAL STORAGE
        val sharedPreferences = getSharedPreferences("PayStreamPrefs", Context.MODE_PRIVATE)
        val savedToken = sharedPreferences.getString("JWT_TOKEN", null)

        if (savedToken == null) {
            Toast.makeText(this, "Auth error. Please log in again.", Toast.LENGTH_SHORT).show()
            return
        }

        val realAuthHeader = "Bearer $savedToken"

        // Send the real header to the server
        RetrofitClient.instance.getEmployees(realAuthHeader).enqueue(object : Callback<List<EmployeeResponse>> {
            override fun onResponse(call: Call<List<EmployeeResponse>>, response: Response<List<EmployeeResponse>>) {
                if (response.isSuccessful && response.body() != null) {
                    completeList = response.body()!!
                    adapter.updateData(completeList)
                } else {
                    Toast.makeText(this@AdminDashboardActivity, "Error pulling directory records", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<EmployeeResponse>>, t: Throwable) {
                Toast.makeText(this@AdminDashboardActivity, "Network failure: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupSearchFilter() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase().trim()

                val filteredList = completeList.filter {
                    // 🟢 THE FIX: Filter against firstname and lastname
                    val firstName = it.user.firstname?.lowercase() ?: ""
                    val lastName = it.user.lastname?.lowercase() ?: ""
                    val fullName = "$firstName $lastName"

                    fullName.contains(query) || it.position.lowercase().contains(query)
                }
                adapter.updateData(filteredList)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun showEditEmployeeModal(employee: EmployeeResponse) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_employee, null)
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false) // Forces them to click Cancel or Update
            .create()

        // Bind Views
        val etName = dialogView.findViewById<EditText>(R.id.etStaffName)
        val etDebt = dialogView.findViewById<EditText>(R.id.etDebt)
        val spinnerPosition = dialogView.findViewById<android.widget.Spinner>(R.id.spinnerPosition)
        val spinnerStatus = dialogView.findViewById<android.widget.Spinner>(R.id.spinnerStatus)
        val btnCancel = dialogView.findViewById<android.widget.Button>(R.id.btnCancel)
        val btnUpdate = dialogView.findViewById<android.widget.Button>(R.id.btnUpdate)

        // Populate Existing Data
        val firstName = employee.user.firstname ?: ""
        val lastName = employee.user.lastname ?: ""
        etName.setText("$firstName $lastName".trim())
        etDebt.setText(employee.debt?.toString() ?: "0")

        // Setup Dropdowns
        val positions = arrayOf("DRIVER", "HELPER", "UNASSIGNED")
        spinnerPosition.adapter = android.widget.ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, positions)
        spinnerPosition.setSelection(positions.indexOf(employee.position.uppercase()).takeIf { it >= 0 } ?: 0)

        val statuses = arrayOf("ACTIVE", "INACTIVE")
        spinnerStatus.adapter = android.widget.ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, statuses)
        spinnerStatus.setSelection(statuses.indexOf(employee.status.uppercase()).takeIf { it >= 0 } ?: 0)

        // Click Listeners
        btnCancel.setOnClickListener { dialog.dismiss() }

        btnUpdate.setOnClickListener {
            // Disable button to prevent double-clicks
            btnUpdate.isEnabled = false
            btnUpdate.text = "Saving..."

            val updatedDebt = etDebt.text.toString().toBigDecimalOrNull() ?: java.math.BigDecimal.ZERO
            val updatedPosition = spinnerPosition.selectedItem.toString()
            val updatedStatus = spinnerStatus.selectedItem.toString()

            val updateRequest = edu.cit.velasco.paystream.features.employee.EmployeeUpdateRequest(
                debt = updatedDebt,
                position = updatedPosition,
                status = updatedStatus
            )

            // Send to Spring Boot
            val token = getSharedPreferences("PayStreamPrefs", MODE_PRIVATE).getString("JWT_TOKEN", null)
            RetrofitClient.instance.updateEmployee("Bearer $token", employee.id, updateRequest)
                .enqueue(object : Callback<EmployeeResponse> {
                    override fun onResponse(call: Call<EmployeeResponse>, response: Response<EmployeeResponse>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@AdminDashboardActivity, "Profile Updated", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                            fetchEmployeeDirectory() // Refresh the list to show new data!
                        } else {
                            btnUpdate.isEnabled = true
                            btnUpdate.text = "Update Profile"
                            Toast.makeText(this@AdminDashboardActivity, "Update Failed", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<EmployeeResponse>, t: Throwable) {
                        btnUpdate.isEnabled = true
                        btnUpdate.text = "Update Profile"
                        Toast.makeText(this@AdminDashboardActivity, "Network Error", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        dialog.show()
    }
}