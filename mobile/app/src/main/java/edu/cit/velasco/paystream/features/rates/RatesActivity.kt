package edu.cit.velasco.paystream.features.rates

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import edu.cit.velasco.paystream.R
import edu.cit.velasco.paystream.core.HeaderManager
import edu.cit.velasco.paystream.core.RetrofitClient
import java.math.BigDecimal
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RatesActivity : AppCompatActivity() {

    // Driver View Form Elements
    private lateinit var etDriverBase: EditText
    private lateinit var etDriver40ft: EditText
    private lateinit var etDriver20ft: EditText
    private lateinit var etDriverOtHour: EditText
    private lateinit var etDriverOtContainer: EditText
    private lateinit var etDriverSss: EditText
    private lateinit var etDriverPhilhealth: EditText
    private lateinit var etDriverPagibig: EditText
    private lateinit var btnSaveDriver: Button

    // Helper View Form Elements
    private lateinit var etHelperBase: EditText
    private lateinit var etHelper40ft: EditText
    private lateinit var etHelper20ft: EditText
    private lateinit var etHelperOtHour: EditText
    private lateinit var etHelperOtContainer: EditText
    private lateinit var etHelperSss: EditText
    private lateinit var etHelperPhilhealth: EditText
    private lateinit var etHelperPagibig: EditText
    private lateinit var btnSaveHelper: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rates)

        HeaderManager.setupHeader(this, "Rates")

        initializeViews()
        loadExistingRates()

        btnSaveDriver.setOnClickListener { executeRateUpdate("DRIVER") }
        btnSaveHelper.setOnClickListener { executeRateUpdate("HELPER") }
    }

    private fun initializeViews() {
        etDriverBase = findViewById(R.id.etDriverBase)
        etDriver40ft = findViewById(R.id.etDriver40ft)
        etDriver20ft = findViewById(R.id.etDriver20ft)
        etDriverOtHour = findViewById(R.id.etDriverOtHour)
        etDriverOtContainer = findViewById(R.id.etDriverOtContainer)
        etDriverSss = findViewById(R.id.etDriverSss)
        etDriverPhilhealth = findViewById(R.id.etDriverPhilhealth)
        etDriverPagibig = findViewById(R.id.etDriverPagibig)
        btnSaveDriver = findViewById(R.id.btnSaveDriver)

        etHelperBase = findViewById(R.id.etHelperBase)
        etHelper40ft = findViewById(R.id.etHelper40ft)
        etHelper20ft = findViewById(R.id.etHelper20ft)
        etHelperOtHour = findViewById(R.id.etHelperOtHour)
        etHelperOtContainer = findViewById(R.id.etHelperOtContainer)
        etHelperSss = findViewById(R.id.etHelperSss)
        etHelperPhilhealth = findViewById(R.id.etHelperPhilhealth)
        etHelperPagibig = findViewById(R.id.etHelperPagibig)
        btnSaveHelper = findViewById(R.id.btnSaveHelper)
    }

    private fun loadExistingRates() {
        // 🟢 1. Grab the real token
        val token = getSharedPreferences("PayStreamPrefs", Context.MODE_PRIVATE).getString("JWT_TOKEN", null)
        if (token == null) return

        RetrofitClient.instance.getRates("Bearer $token").enqueue(object : Callback<List<PayRatesRequest>> {
            override fun onResponse(call: Call<List<PayRatesRequest>>, response: Response<List<PayRatesRequest>>) {
                if (response.isSuccessful && response.body() != null) {

                    // 🟢 2. Fill the empty boxes with the real database data
                    for (rate in response.body()!!) {
                        if (rate.position?.uppercase() == "DRIVER") {
                            etDriverBase.setText(rate.baseRate.toPlainString())
                            etDriver40ft.setText(rate.rate40ft.toPlainString())
                            etDriver20ft.setText(rate.rate20ft.toPlainString())
                            etDriverOtHour.setText(rate.rateOtHour.toPlainString())
                            etDriverOtContainer.setText(rate.rateOtContainer.toPlainString())
                            etDriverSss.setText(rate.rateSss.toPlainString())
                            etDriverPhilhealth.setText(rate.ratePhilhealth.toPlainString())
                            etDriverPagibig.setText(rate.ratePagibig.toPlainString())
                        } else if (rate.position?.uppercase() == "HELPER") {
                            etHelperBase.setText(rate.baseRate.toPlainString())
                            etHelper40ft.setText(rate.rate40ft.toPlainString())
                            etHelper20ft.setText(rate.rate20ft.toPlainString())
                            etHelperOtHour.setText(rate.rateOtHour.toPlainString())
                            etHelperOtContainer.setText(rate.rateOtContainer.toPlainString())
                            etHelperSss.setText(rate.rateSss.toPlainString())
                            etHelperPhilhealth.setText(rate.ratePhilhealth.toPlainString())
                            etHelperPagibig.setText(rate.ratePagibig.toPlainString())
                        }
                    }
                }
            }
            override fun onFailure(call: Call<List<PayRatesRequest>>, t: Throwable) {
                Toast.makeText(this@RatesActivity, "Failed to load active rates", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun executeRateUpdate(position: String) {
        val token = getSharedPreferences("PayStreamPrefs", Context.MODE_PRIVATE).getString("JWT_TOKEN", null)
        if (token == null) return

        // 🟢 3. Safely convert inputs. If empty, default to 0 instead of crashing.
        val payload = if (position == "DRIVER") {
            PayRatesRequest(
                position = position,
                baseRate = etDriverBase.text.toString().toBigDecimalOrNull() ?: BigDecimal.ZERO,
                rate40ft = etDriver40ft.text.toString().toBigDecimalOrNull() ?: BigDecimal.ZERO,
                rate20ft = etDriver20ft.text.toString().toBigDecimalOrNull() ?: BigDecimal.ZERO,
                rateOtContainer = etDriverOtContainer.text.toString().toBigDecimalOrNull() ?: BigDecimal.ZERO,
                rateOtHour = etDriverOtHour.text.toString().toBigDecimalOrNull() ?: BigDecimal.ZERO,
                rateSss = etDriverSss.text.toString().toBigDecimalOrNull() ?: BigDecimal.ZERO,
                ratePhilhealth = etDriverPhilhealth.text.toString().toBigDecimalOrNull() ?: BigDecimal.ZERO,
                ratePagibig = etDriverPagibig.text.toString().toBigDecimalOrNull() ?: BigDecimal.ZERO
            )
        } else {
            PayRatesRequest(
                position = position,
                baseRate = etHelperBase.text.toString().toBigDecimalOrNull() ?: BigDecimal.ZERO,
                rate40ft = etHelper40ft.text.toString().toBigDecimalOrNull() ?: BigDecimal.ZERO,
                rate20ft = etHelper20ft.text.toString().toBigDecimalOrNull() ?: BigDecimal.ZERO,
                rateOtContainer = etHelperOtContainer.text.toString().toBigDecimalOrNull() ?: BigDecimal.ZERO,
                rateOtHour = etHelperOtHour.text.toString().toBigDecimalOrNull() ?: BigDecimal.ZERO,
                rateSss = etHelperSss.text.toString().toBigDecimalOrNull() ?: BigDecimal.ZERO,
                ratePhilhealth = etHelperPhilhealth.text.toString().toBigDecimalOrNull() ?: BigDecimal.ZERO,
                ratePagibig = etHelperPagibig.text.toString().toBigDecimalOrNull() ?: BigDecimal.ZERO
            )
        }

        RetrofitClient.instance.updateRates("Bearer $token", position, payload).enqueue(object : Callback<PayRatesRequest> {
            override fun onResponse(call: Call<PayRatesRequest>, response: Response<PayRatesRequest>) {
                if (response.isSuccessful) {
                    showSuccessMessage(position)
                } else {
                    Toast.makeText(this@RatesActivity, "Error saving updates", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<PayRatesRequest>, t: Throwable) {
                Toast.makeText(this@RatesActivity, "Network execution failed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showSuccessMessage(role: String) {
        Toast.makeText(this@RatesActivity, "$role configurations saved successfully!", Toast.LENGTH_SHORT).show()
    }
}