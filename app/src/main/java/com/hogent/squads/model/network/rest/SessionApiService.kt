package com.hogent.squads.model.network.rest

import com.hogent.squads.model.network.rest.resources.JoinRequest
import com.hogent.squads.model.network.rest.resources.JoinSessionResponse
import com.hogent.squads.model.network.rest.resources.SessionsResponse
import retrofit2.Call
import retrofit2.http.*

interface SessionApiService {

    @GET("/api/session")
    fun getAllSessions(@Query("IsDone") isDone: String): Call<SessionsResponse>

    @PUT("/api/session/join")
    fun joinSession(@Body joinRequest: JoinRequest): Call<JoinSessionResponse>

    @PUT("/api/session/leave")
    fun leaveSession(@Body joinRequest: JoinRequest): Call<JoinSessionResponse>
}
