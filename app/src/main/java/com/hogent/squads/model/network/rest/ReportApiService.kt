package com.hogent.squads.model.network.rest

import com.hogent.squads.model.network.rest.resources.ReportsResponse
import retrofit2.Call
import retrofit2.http.*

interface ReportApiService {

    @GET("/api/report/forUser/{userId}")
    fun getReportsForUser(@Path("userId") userId: Int): Call<ReportsResponse>
}
