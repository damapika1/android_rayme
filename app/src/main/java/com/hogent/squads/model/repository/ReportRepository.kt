package com.hogent.squads.model.repository

import androidx.lifecycle.LiveData
import com.hogent.squads.model.database.ReportDao
import com.hogent.squads.model.domain.Report
import com.hogent.squads.model.domain.Session
import com.hogent.squads.model.network.rest.ReportApiService
import com.hogent.squads.model.network.rest.resources.ReportsResponse
import retrofit2.Call
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepository @Inject constructor(
    private val reportApiService: ReportApiService,
    private val reportDao: ReportDao
) {

    fun getReportsForUserFromApi(userId: Int): Call<ReportsResponse> {
        return reportApiService.getReportsForUser(userId)
    }

    fun getReportsForUser(userId: Int?): LiveData<List<Report>> {
        return reportDao.getReportsByUserId(userId)
    }

    suspend fun saveReports(reports: List<Report>) {
        reportDao.insertData(reports)
    }

    suspend fun deleteAll() {
        reportDao.deleteAll()
    }

}
