package com.hogent.squads.model.network.rest

import com.hogent.squads.model.network.rest.resources.EditUserRequest
import com.hogent.squads.model.network.rest.resources.EditUserResponse
import com.hogent.squads.model.network.rest.resources.LoginRequest
import com.hogent.squads.model.network.rest.resources.LoginResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApiService {

    @Headers("Content-Type:application/json")
    @POST("/api/auth/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @GET("/api/user/{id}")
    suspend fun getUserInfo(@Path("id") id:Int): LoginResponse

    @Headers("Content-Type:application/json")
    @PUT("/api/user/")
    suspend fun editUser(@Body editUser: EditUserRequest): EditUserResponse
}
