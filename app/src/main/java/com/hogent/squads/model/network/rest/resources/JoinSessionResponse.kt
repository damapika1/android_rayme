package com.hogent.squads.model.network.rest.resources

import com.google.gson.annotations.SerializedName

data class JoinSessionResponse(
    @SerializedName("sessionId")
    val sessionId: Int
)
