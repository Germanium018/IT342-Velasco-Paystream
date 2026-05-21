package edu.cit.velasco.paystream.features.payroll

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.cit.velasco.paystream.R
import java.text.NumberFormat
import java.util.Locale

class PayslipAdapter(
    private var payslips: List<PayrollTransactionResponse>,
    private val onViewClick: (PayrollTransactionResponse) -> Unit,
    private val onDownloadClick: (PayrollTransactionResponse) -> Unit
) : RecyclerView.Adapter<PayslipAdapter.PayslipViewHolder>() {

    class PayslipViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvEmployeeName)
        val tvRole: TextView = view.findViewById(R.id.tvEmployeeRole)
        val tvDate: TextView = view.findViewById(R.id.tvPayslipDate)
        val tvNetPay: TextView = view.findViewById(R.id.tvNetPayAmount)
        val btnView: ImageView = view.findViewById(R.id.btnViewDetails)
        val btnDownload: ImageView = view.findViewById(R.id.btnDownloadPdf)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PayslipViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_payslip_card, parent, false)
        return PayslipViewHolder(view)
    }

    override fun onBindViewHolder(holder: PayslipViewHolder, position: Int) {
        val payslip = payslips[position]

        // 🟢 THE FIX: Combine first and last name safely
        val firstName = payslip.employee.user.firstname ?: "Unknown"
        val lastName = payslip.employee.user.lastname ?: ""
        holder.tvName.text = "$firstName $lastName".trim()

        holder.tvRole.text = payslip.employee.position
        holder.tvRole.visibility = View.GONE

        // Fallback to monthYear if the processedAt string is missing
        holder.tvDate.text = payslip.processedAt?.take(10) ?: payslip.monthYear

        // Format Net Pay to Philippine Peso
        val format = NumberFormat.getCurrencyInstance(Locale("phi", "PH"))
        holder.tvNetPay.text = format.format(payslip.netPay)

        // 2. Wire up the interactive buttons to the Activity
        holder.btnView.setOnClickListener { onViewClick(payslip) }
        holder.btnDownload.setOnClickListener { onDownloadClick(payslip) }
    }

    override fun getItemCount(): Int = payslips.size

    // Used for the search bar filtering feature later
    fun updateData(newList: List<PayrollTransactionResponse>) {
        payslips = newList
        notifyDataSetChanged()
    }
}