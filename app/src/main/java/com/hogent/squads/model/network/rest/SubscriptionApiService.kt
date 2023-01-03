package com.hogent.squads.model.network.rest

import com.hogent.squads.model.network.rest.resources.ExtendSubscriptionRequest
import com.hogent.squads.model.network.rest.resources.SubscriptionDetailResponse
import com.hogent.squads.model.network.rest.resources.SubscriptionResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SubscriptionApiService {
    @GET("api/subscription")
    fun getSubscriptionsForUser(@Query("userId") userId:Int): Call<SubscriptionResponse>

    @POST("api/subscription")
    fun extendUserSubscription(@Body extendRequest:ExtendSubscriptionRequest): Call<SubscriptionDetailResponse>
}