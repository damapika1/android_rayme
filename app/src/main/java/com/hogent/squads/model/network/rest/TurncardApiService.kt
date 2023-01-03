package com.hogent.squads.model.network.rest

import com.hogent.squads.model.network.rest.resources.IncreaseTurnsRequest
import com.hogent.squads.model.network.rest.resources.TurncardDetailResponse
import com.hogent.squads.model.network.rest.resources.TurncardResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface TurncardApiService {

    @GET("api/turncard")
    fun getTurncardsForUser(@Query("userId") userId:Int) : Call<TurncardResponse>

    @POST("api/turncard")
    fun increaseTurnsForUser(@Body req:IncreaseTurnsRequest) : Call<TurncardDetailResponse>
}