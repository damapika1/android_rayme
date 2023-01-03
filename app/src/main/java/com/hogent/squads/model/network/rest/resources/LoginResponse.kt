package com.hogent.squads.model.network.rest.resources

import com.google.gson.annotations.SerializedName
import com.hogent.squads.model.domain.PricePlan
import com.hogent.squads.model.domain.User
import java.util.*

data class LoginResponse(
    @SerializedName("token")
    var token: String,

    @SerializedName("user")
    var userData: UserData
) {
    fun mapToUser(): User {
        val random = Random()
        val randomImageIndex: Int = random.nextInt(79)
        val imageUrl: String = if (randomImageIndex % 2 == 0) {
            String.format("https://xsgames.co/randomusers/assets/avatars/male/%d.jpg", randomImageIndex)
        } else {
            String.format("https://xsgames.co/randomusers/assets/avatars/female/%d.jpg", randomImageIndex)
        }
        return User(
            this.userData.id,
            this.userData.email,
            null,
            this.userData.firstName,
            this.userData.lastName,
            this.userData.address,
            this.userData.phoneNumber,
            imageUrl,
            this.userData.activePricePlan
        )
    }
}

data class UserData(
    @SerializedName("id")
    val id: Int,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("address")
    val address: String?,
    @SerializedName("phoneNumber")
    val phoneNumber: String?,
    val activePricePlan: PricePlan
)
