package com.hogent.squads.model.network.rest.resources

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val user: UserCredentials
)

data class UserCredentials(
    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String
)
