package com.hogent.squads.model.network.rest.resources

import androidx.room.ColumnInfo
import com.google.gson.annotations.SerializedName
import com.hogent.squads.model.domain.Report
import java.time.LocalDateTime
import java.util.*
import kotlin.streams.toList

data class ReportsResponse(
    @SerializedName("reports")
    val reports: List<ReportData>
) {
    fun toReportList(currentUserId: Int): List<Report> {
        return reports.stream()
            .map { reportData ->
                    Report(
                        reportData.id,
                        currentUserId,
                        LocalDateTime.parse(reportData.date),
                        reportData.weight,
                        reportData.fatPercentage,
                        reportData.waistSize,
                        reportData.musclePercentage
                    )
            }
            .toList()
    }
}

data class ReportData(
    @SerializedName("id")
    val id: Int,
    @SerializedName("date")
    val date: String,
    @SerializedName("weight")
    val weight: Double,
    @ColumnInfo(name = "fatPercentage")
    val fatPercentage: Double,
    @SerializedName("waistSize")
    val waistSize: Double,
    @SerializedName("musclePercentage")
    val musclePercentage: Double
)
