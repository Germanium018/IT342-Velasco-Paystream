package edu.cit.velasco.paystream.features.payroll

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.cit.velasco.paystream.R
import java.text.DecimalFormat

class EmployeePayslipAdapter(
    private var payslips: List<PayrollTransactionResponse>,
    private val onViewClick: (PayrollTransactionResponse) -> Unit,
    private val onDownloadClick: (PayrollTransactionResponse) -> Unit
) : RecyclerView.Adapter<EmployeePayslipAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tvPayslipDate)
        val tvNetPay: TextView = view.findViewById(R.id.tvNetPayAmount)
        val btnView: ImageView = view.findViewById(R.id.btnViewDetails)
        val btnDownload: ImageView = view.findViewById(R.id.btnDownloadPdf)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_employee_payslip_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val payslip = payslips[position]
        val df = DecimalFormat("#,##0.00")

        holder.tvDate.text = payslip.processedAt?.take(10) ?: payslip.monthYear
        holder.tvNetPay.text = "Php ${df.format(payslip.netPay)}"

        holder.btnView.setOnClickListener { onViewClick(payslip) }
        holder.btnDownload.setOnClickListener { onDownloadClick(payslip) }
    }

    override fun getItemCount(): Int = payslips.size

    fun updateData(newList: List<PayrollTransactionResponse>) {
        payslips = newList
        notifyDataSetChanged()
    }
}