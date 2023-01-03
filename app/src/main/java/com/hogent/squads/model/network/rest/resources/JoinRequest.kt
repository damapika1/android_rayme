package com.hogent.squads.model.network.rest.resources
import com.google.gson.annotations.SerializedName

data class JoinRequest(
    @SerializedName("sessionId")
    val sessionId:Int,

    @SerializedName("mutateTrainees")
    val mutateTrainees:TraineesData
)
