package com.hogent.squads.model.domain

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

@Entity(tableName = "User")
data class User(
    @PrimaryKey()
    @ColumnInfo(name = "userId")
    val userId: Int = 0,
    val email: String = "test@mail.com",
    var sessionCreatorId: Int?,
    val firstName: String?="",
    val lastName: String?="",
    val address: String?="",
    val phoneNumber: String?="",
    val imageUrl: String?="",
    @Embedded var pricePlan: PricePlan=PricePlan()
)

class TypeConverterUser {
    private val gson: Gson = Gson()

    @TypeConverter
    fun jsonToUser(json: String?): User? {
        if (json == null) return null
        val listType: Type = object : TypeToken<User>() {}.type
        return gson.fromJson<User>(json, listType)
    }

    @TypeConverter
    fun userToJson(user: User): String? {
        return gson.toJson(user)
    }
}
