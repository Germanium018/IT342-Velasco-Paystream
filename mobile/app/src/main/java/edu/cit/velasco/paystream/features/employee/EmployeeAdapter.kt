package edu.cit.velasco.paystream.features.employee

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.cit.velasco.paystream.R
import java.text.NumberFormat
import java.util.Locale

class EmployeeAdapter(
    private var employees: List<EmployeeResponse>,
    private val onEditDebtClicked: (EmployeeResponse) -> Unit
) : RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder>() {

    class EmployeeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvEmployeeName)
        val position: TextView = view.findViewById(R.id.tvEmployeePosition)
        val status: TextView = view.findViewById(R.id.tvStatusBadge)
        val debt: TextView = view.findViewById(R.id.tvDebtAmount)
        val editBtn: ImageView = view.findViewById(R.id.btnEditDebt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_employee, parent, false)
        return EmployeeViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        val employee = employees[position]

        // 🟢 THE FIX: Combine firstname and lastname safely
        val firstName = employee.user.firstname ?: "Unknown"
        val lastName = employee.user.lastname ?: ""
        holder.name.text = "$firstName $lastName".trim()

        holder.position.text = employee.position.lowercase().replaceFirstChar { it.uppercase() }

        // Format Currency dynamically to Philippine Peso
        val format = NumberFormat.getCurrencyInstance(Locale("phi", "PH"))
        holder.debt.text = format.format(employee.debt ?: java.math.BigDecimal.ZERO)

        // Bind Status Badges & Colors dynamically
        if (employee.status.equals("ACTIVE", ignoreCase = true)) {
            holder.status.text = "Active"
            holder.status.setTextColor(Color.parseColor("#16A34A")) // Green
            holder.status.setBackgroundColor(Color.parseColor("#DCFCE7"))
        } else {
            holder.status.text = "Inactive"
            holder.status.setTextColor(Color.parseColor("#DC2626")) // Red
            holder.status.setBackgroundColor(Color.parseColor("#FEE2E2"))
        }

        holder.editBtn.setOnClickListener { onEditDebtClicked(employee) }
    }

    override fun getItemCount() = employees.size

    fun updateData(newEmployees: List<EmployeeResponse>) {
        this.employees = newEmployees
        notifyDataSetChanged()
    }
}