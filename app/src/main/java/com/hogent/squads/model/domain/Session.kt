package com.hogent.squads.model.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.time.LocalDateTime

@Entity(tableName="Sessions")
data class Session(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "sessionId")
    var sessionId: Int,
    @ColumnInfo(name = "Title")
    val title: String,
    @ColumnInfo(name = "Description")
    val description: String? = "",
    @ColumnInfo(name = "StartsAt")
    val startsAt: LocalDateTime,
    @ColumnInfo(name = "EndsAt")
    val endsAt: LocalDateTime,
    @ColumnInfo(name = "WorkoutType")
    val workoutType: String,
    @ColumnInfo(name = "Trainees")
    var trainees: Int,
    @ColumnInfo(name = "userFK")
    var userFK: Int?
) {

    fun joinSession(userId: String) {
        trainees += 1
        userFK = userId.toInt()
    }

    fun leaveSession() {
        trainees -= 1
        userFK = null
    }
}

class TypeConverterSession {
    private val gson: Gson = Gson()
    @TypeConverter
    fun jsonToSession(json: String?): Session? {
        if (json == null) return null
        val listType: Type = object : TypeToken<Session>() {}.type
        return gson.fromJson<Session>(json, listType)
    }

    @TypeConverter
    fun sessionToJson(sessionData: Session): String? {
        return gson.toJson(sessionData)
    }

    @TypeConverter
    fun fromLocalDateToString(value: String?): LocalDateTime? {
        return value?.let {
            return LocalDateTime.parse(value)
        }
    }

    @TypeConverter
    fun stringToLocalDate(date: LocalDateTime?): String? {
        return date.toString()
    }
}
