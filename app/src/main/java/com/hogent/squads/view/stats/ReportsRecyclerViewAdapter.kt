package com.hogent.squads.view.stats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hogent.squads.R
import com.hogent.squads.model.domain.Report
import java.time.format.DateTimeFormatter

class ReportsRecyclerViewAdapter : RecyclerView.Adapter<ReportViewHolder>() {

    private var reportList: List<Report>? = null

    fun setReportList(reportList: List<Report>) {
        this.reportList = reportList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val layoutId = R.layout.component_report

        val layoutView = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return ReportViewHolder(layoutView)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        if (reportList != null && position < reportList!!.size) {
            val reportEntry = reportList!![position]
            holder.bind(reportEntry)
        }
    }

    override fun getItemCount(): Int {
        return reportList?.size ?: 0
    }

}

class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val reportDate: TextView = itemView.findViewById(R.id.report_Date)
    private val reportWeight: TextView = itemView.findViewById(R.id.report_Weight)
    private val reportFatPercent: TextView = itemView.findViewById(R.id.report_FatPercent)
    private val reportWaist: TextView = itemView.findViewById(R.id.report_Waist)
    private val reportMuscle: TextView = itemView.findViewById(R.id.report_MusclePercent)

    fun bind(report: Report) {
        reportDate.text = report.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        reportWeight.text = String.format("%.2f", report.weight)
        reportFatPercent.text = String.format("%.2f", report.fatPercentage)
        reportWaist.text = String.format("%.2f", report.waistSize)
        reportMuscle.text = String.format("%.2f", report.musclePercentage)
    }
}
