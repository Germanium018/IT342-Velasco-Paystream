package edu.cit.velasco.paystream.features.payroll

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import edu.cit.velasco.paystream.R
import edu.cit.velasco.paystream.core.RetrofitClient
import edu.cit.velasco.paystream.features.employee.EmployeeResponse
import edu.cit.velasco.paystream.features.rates.PayRatesRequest
import java.math.BigDecimal
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import edu.cit.velasco.paystream.core.HeaderManager

class PayrollProcessingActivity : AppCompatActivity() {

    private lateinit var spinnerEmployee: Spinner
    private lateinit var btnGenerate: Button
    private lateinit var tvNetPay: TextView

    private lateinit var cbSss: CheckBox
    private lateinit var cbPhilhealth: CheckBox
    private lateinit var cbPagibig: CheckBox

    // 🟢 SPLIT PREVIEW VIEWS TO MATCH WEB
    private lateinit var previewBase: TextView
    private lateinit var preview40ft: TextView
    private lateinit var preview20ft: TextView
    private lateinit var previewOtHours: TextView
    private lateinit var previewOtContainers: TextView
    private lateinit var previewOutTown: TextView
    private lateinit var previewGross: TextView
    private lateinit var previewSss: TextView
    private lateinit var previewPhilhealth: TextView
    private lateinit var previewPagibig: TextView
    private lateinit var previewAbsences: TextView
    private lateinit var previewAdvances: TextView
    private lateinit var previewOtherDebts: TextView

    private var employeeList: List<EmployeeResponse> = listOf()
    private var activeRatesList: List<PayRatesRequest> = listOf()
    private var selectedEmployee: EmployeeResponse? = null

    private var daysWorked = BigDecimal.ZERO
    private var count40ft = BigDecimal.ZERO
    private var count20ft = BigDecimal.ZERO
    private var otHours = BigDecimal.ZERO
    private var otContainers = BigDecimal.ZERO
    private var tripsOut = BigDecimal.ZERO
    private var cashAdvances = BigDecimal.ZERO
    private var totalAbsences = BigDecimal.ZERO
    private var remainingDebts = BigDecimal.ZERO
    private var computedNetPay = BigDecimal.ZERO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payroll_processing)

        HeaderManager.setupHeader(this, "Payroll")

        initializeFormControls()
        setupAdjustmentSteppers()
        loadCoreDataPipeline()

        btnGenerate.setOnClickListener { executePayrollSubmission() }
    }

    private fun initializeFormControls() {
        spinnerEmployee = findViewById(R.id.spEmployeeSelector)
        cbSss = findViewById(R.id.cbSss)
        cbPhilhealth = findViewById(R.id.cbPhilhealth)
        cbPagibig = findViewById(R.id.cbPagibig)

        tvNetPay = findViewById(R.id.tvNetTakeHomePay)

        // 🟢 BINDING THE NEW SPLIT VIEWS
        previewBase = findViewById(R.id.previewBase)
        preview40ft = findViewById(R.id.preview40ft)
        preview20ft = findViewById(R.id.preview20ft)
        previewOtHours = findViewById(R.id.previewOtHours)
        previewOtContainers = findViewById(R.id.previewOtContainers)
        previewOutTown = findViewById(R.id.previewOutTown)
        previewGross = findViewById(R.id.previewGrossPay)
        previewSss = findViewById(R.id.previewSss)
        previewPhilhealth = findViewById(R.id.previewPhilhealth)
        previewPagibig = findViewById(R.id.previewPagibig)
        previewAbsences = findViewById(R.id.previewAbsences)
        previewAdvances = findViewById(R.id.previewAdvances)
        previewOtherDebts = findViewById(R.id.previewOtherDebts)
        btnGenerate = findViewById(R.id.btnGeneratePayslip)

        val checkboxListener = CompoundButton.OnCheckedChangeListener { _, _ -> evaluateLiveCalculations() }
        cbSss.setOnCheckedChangeListener(checkboxListener)
        cbPhilhealth.setOnCheckedChangeListener(checkboxListener)
        cbPagibig.setOnCheckedChangeListener(checkboxListener)
    }

    private fun setupAdjustmentSteppers() {
        // Most steppers have no maximum limit, so we pass { null }
        bindStepper(R.id.btnMinusWorkingDays, R.id.btnPlusWorkingDays, R.id.tvWorkingDaysCount, { null }) { daysWorked = it }
        bindStepper(R.id.btnMinus40ft, R.id.btnPlus40ft, R.id.tv40ftCount, { null }) { count40ft = it }
        bindStepper(R.id.btnMinus20ft, R.id.btnPlus20ft, R.id.tv20ftCount, { null }) { count20ft = it }
        bindStepper(R.id.btnMinusOtHours, R.id.btnPlusOtHours, R.id.tvOtHoursCount, { null }) { otHours = it }
        bindStepper(R.id.btnMinusOtContainers, R.id.btnPlusOtContainers, R.id.tvOtContainersCount, { null }) { otContainers = it }
        bindStepper(R.id.btnMinusOutOfTown, R.id.btnPlusOutOfTown, R.id.tvOutOfTownCount, { null }) { tripsOut = it }
        bindStepper(R.id.btnMinusCashAdvance, R.id.btnPlusCashAdvance, R.id.tvCashAdvanceCount, { null }) { cashAdvances = it }
        bindStepper(R.id.btnMinusAbsences, R.id.btnPlusAbsences, R.id.tvAbsencesCount, { null }) { totalAbsences = it }

        // 🟢 THE MAGIC: Limit "Other Debts" strictly to the employee's database debt value!
        bindStepper(R.id.btnMinusOtherDebts, R.id.btnPlusOtherDebts, R.id.tvOtherDebtsCount, { selectedEmployee?.debt ?: BigDecimal.ZERO }) { remainingDebts = it }
    }

    // 🟢 UPDATED UX: Tap to type + Logic to block values over the limit
    private fun bindStepper(minusId: Int, plusId: Int, textId: Int, maxLimit: () -> BigDecimal?, updateState: (BigDecimal) -> Unit) {
        val txtCount = findViewById<TextView>(textId)

        // 1. Minus Button
        findViewById<Button>(minusId).setOnClickListener {
            val current = txtCount.text.toString().toBigDecimalOrNull() ?: BigDecimal.ZERO
            if (current > BigDecimal.ZERO) {
                val nextValue = current - BigDecimal.ONE
                txtCount.text = nextValue.toPlainString()
                updateState(nextValue)
                evaluateLiveCalculations()
            }
        }

        // 2. Plus Button (With Limit Check)
        findViewById<Button>(plusId).setOnClickListener {
            val current = txtCount.text.toString().toBigDecimalOrNull() ?: BigDecimal.ZERO
            val limit = maxLimit()
            if (limit != null && current >= limit) {
                Toast.makeText(this@PayrollProcessingActivity, "Capped at outstanding debt of ₱$limit", Toast.LENGTH_SHORT).show()
            } else {
                val nextValue = current + BigDecimal.ONE
                txtCount.text = nextValue.toPlainString()
                updateState(nextValue)
                evaluateLiveCalculations()
            }
        }

        // 3. Tap to Type Directly!
        txtCount.setOnClickListener {
            val inputEditTextField = EditText(this)
            inputEditTextField.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            inputEditTextField.setText(txtCount.text.toString())
            inputEditTextField.setSelection(inputEditTextField.text.length)

            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Enter Amount")
                .setView(inputEditTextField)
                .setPositiveButton("Set") { _, _ ->
                    var value = inputEditTextField.text.toString().toBigDecimalOrNull() ?: BigDecimal.ZERO
                    val limit = maxLimit()
                    if (limit != null && value > limit) {
                        value = limit
                        Toast.makeText(this@PayrollProcessingActivity, "Value capped to match outstanding debt: ₱$limit", Toast.LENGTH_LONG).show()
                    }
                    txtCount.text = value.toPlainString()
                    updateState(value)
                    evaluateLiveCalculations()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun loadCoreDataPipeline() {
        val token = getSharedPreferences("PayStreamPrefs", Context.MODE_PRIVATE).getString("JWT_TOKEN", null)
        if (token == null) return
        val realToken = "Bearer $token"

        RetrofitClient.instance.getRates(realToken).enqueue(object : Callback<List<PayRatesRequest>> {
            override fun onResponse(call: Call<List<PayRatesRequest>>, response: Response<List<PayRatesRequest>>) {
                if (response.isSuccessful && response.body() != null) {
                    activeRatesList = response.body()!!

                    RetrofitClient.instance.getEmployees(realToken).enqueue(object : Callback<List<EmployeeResponse>> {
                        override fun onResponse(call: Call<List<EmployeeResponse>>, res: Response<List<EmployeeResponse>>) {
                            if (res.isSuccessful && res.body() != null) {

                                // 🟢 THE FIX: Filter the database list to ONLY include ACTIVE employees!
                                employeeList = res.body()!!.filter { it.status.equals("ACTIVE", ignoreCase = true) }

                                populateEmployeeSpinner()
                            }
                        }
                        override fun onFailure(call: Call<List<EmployeeResponse>>, t: Throwable) {}
                    })
                }
            }
            override fun onFailure(call: Call<List<PayRatesRequest>>, t: Throwable) {}
        })
    }

    private fun populateEmployeeSpinner() {
        val namesList = employeeList.map {
            val firstName = it.user.firstname ?: "Unknown"
            val lastName = it.user.lastname ?: ""
            "$firstName $lastName (${it.position})".trim()
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, namesList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEmployee.adapter = adapter

        spinnerEmployee.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                selectedEmployee = employeeList[pos]
                evaluateLiveCalculations()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun evaluateLiveCalculations() {
        val currentEmp = selectedEmployee ?: return

        val matchedRate = activeRatesList.find { it.position.equals(currentEmp.position, ignoreCase = true) }
            ?: PayRatesRequest(
                position = currentEmp.position,
                baseRate = BigDecimal("600"),
                rate40ft = BigDecimal("200"),
                rate20ft = BigDecimal("200"),
                rateOtContainer = BigDecimal("200"),
                rateOtHour = BigDecimal("200"),
                rateSss = BigDecimal("200"),
                ratePhilhealth = BigDecimal("200"),
                ratePagibig = BigDecimal("200")
            )

        val basicPay = daysWorked.multiply(matchedRate.baseRate)
        val pay40ft = count40ft.multiply(matchedRate.rate40ft)
        val pay20ft = count20ft.multiply(matchedRate.rate20ft)
        val payOtHours = otHours.multiply(matchedRate.rateOtHour)
        val payOtContainers = otContainers.multiply(matchedRate.rateOtContainer)

        val totalGross = basicPay.add(pay40ft).add(pay20ft).add(payOtHours).add(payOtContainers).add(tripsOut)
        val absenceDeduction = totalAbsences.multiply(matchedRate.baseRate)

        val sss = if (cbSss.isChecked) matchedRate.rateSss else BigDecimal.ZERO
        val philhealth = if (cbPhilhealth.isChecked) matchedRate.ratePhilhealth else BigDecimal.ZERO
        val pagibig = if (cbPagibig.isChecked) matchedRate.ratePagibig else BigDecimal.ZERO

        val totalStatutory = sss.add(philhealth).add(pagibig)
        val totalOtherDeductions = absenceDeduction.add(cashAdvances).add(remainingDebts)

        computedNetPay = totalGross.subtract(totalStatutory).subtract(totalOtherDeductions)

        // 🟢 POPULATE THE NEW SPLIT PREVIEW
        val format = NumberFormat.getCurrencyInstance(Locale("phi", "PH"))
        previewBase.text = format.format(basicPay)
        preview40ft.text = format.format(pay40ft)
        preview20ft.text = format.format(pay20ft)
        previewOtHours.text = format.format(payOtHours)
        previewOtContainers.text = format.format(payOtContainers)
        previewOutTown.text = format.format(tripsOut)
        previewGross.text = format.format(totalGross)

        previewSss.text = "-${format.format(sss)}"
        previewPhilhealth.text = "-${format.format(philhealth)}"
        previewPagibig.text = "-${format.format(pagibig)}"
        previewAbsences.text = "-${format.format(absenceDeduction)}"
        previewAdvances.text = "-${format.format(cashAdvances)}"
        previewOtherDebts.text = "-${format.format(remainingDebts)}"
        tvNetPay.text = format.format(computedNetPay)
    }

    private fun executePayrollSubmission() {
        val currentEmp = selectedEmployee ?: return Toast.makeText(this, "Please select an employee", Toast.LENGTH_SHORT).show()
        val token = getSharedPreferences("PayStreamPrefs", Context.MODE_PRIVATE).getString("JWT_TOKEN", null)
        if (token == null) return
        val realToken = "Bearer $token"

        // Fetch the rates to apply the correct statutory deductions dynamically
        val matchedRate = activeRatesList.find { it.position.equals(currentEmp.position, ignoreCase = true) }
            ?: PayRatesRequest(
                position = currentEmp.position,
                baseRate = BigDecimal("600"),
                rate40ft = BigDecimal("200"),
                rate20ft = BigDecimal("200"),
                rateOtContainer = BigDecimal("200"),
                rateOtHour = BigDecimal("200"),
                rateSss = BigDecimal("200"),
                ratePhilhealth = BigDecimal("200"),
                ratePagibig = BigDecimal("200")
            )

        val sss = if (cbSss.isChecked) matchedRate.rateSss else BigDecimal.ZERO
        val philhealth = if (cbPhilhealth.isChecked) matchedRate.ratePhilhealth else BigDecimal.ZERO
        val pagibig = if (cbPagibig.isChecked) matchedRate.ratePagibig else BigDecimal.ZERO

        val currentMonthYear = SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(Date())

        val payload = PayrollTransactionRequest(
            employee = EmployeeIdWrapper(currentEmp.id),
            monthYear = currentMonthYear,
            workingDays = daysWorked,
            count40ft = count40ft,
            count20ft = count20ft,
            overtimeHours = otHours,
            otContainerCount = otContainers,
            outOfTownTrips = tripsOut,
            absences = totalAbsences,
            // 🟢 THE FIX: Replaced hardcoded "200" with dynamic rate variables
            sssDeduction = sss,
            philhealthDeduction = philhealth,
            pagibigDeduction = pagibig,
            cashAdvance = cashAdvances,
            otherDebts = remainingDebts,
            netPay = computedNetPay
        )

        RetrofitClient.instance.processPayroll(realToken, payload).enqueue(object : Callback<PayrollResponse> {
            override fun onResponse(call: Call<PayrollResponse>, response: Response<PayrollResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@PayrollProcessingActivity, "Payslip Generated! Net: ₱${response.body()?.netPay}", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this@PayrollProcessingActivity, "Submission denied by entity service rule mappings", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<PayrollResponse>, t: Throwable) {
                Toast.makeText(this@PayrollProcessingActivity, "Network transaction fault: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}