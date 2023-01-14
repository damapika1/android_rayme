package com.hogent.squads.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hogent.squads.utils.TokenSessionManager
import com.hogent.squads.model.repository.ReportRepository
import com.hogent.squads.model.repository.UserRepository
import com.hogent.squads.model.domain.Report
import com.hogent.squads.model.network.rest.resources.ReportsResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
    private val userRepository: UserRepository,
    private val tokenSessionManager: TokenSessionManager
) : ViewModel() {

    fun fetchReportsForUser(): LiveData<List<Report>> {
        return getCurrentUserId()?.let { userId ->
            reportRepository.getReportsForUserFromApi(userId)
                .enqueue(object : Callback<ReportsResponse?> {
                    override fun onResponse(
                        call: Call<ReportsResponse?>,
                        response: Response<ReportsResponse?>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            viewModelScope.launch {
                                val reports = response.body()!!.toReportList(userId)
                                reportRepository.saveReports(reports)
                            }
                        } else if (response.code() == 401) {
                            viewModelScope.launch {
                                userRepository.deleteCurrentUser();
                            }
                        }
                    }

                    override fun onFailure(call: Call<ReportsResponse?>, error: Throwable) {
                        Timber.i(error)
                    }
                })

            reportRepository.getReportsForUser(userId)
        } ?: reportRepository.getReportsForUser(null)
    }

    fun getCurrentUserId(): Int? {
        return tokenSessionManager.fetchUserId()?.toInt()
    }

    suspend fun deleteAll() {
        reportRepository.deleteAll()
    }

    override fun onCleared() {
        Timber.tag("REPORT_VIEW_MODEL").d("Clearing the viewmodel...")
        super.onCleared()
    }
}
