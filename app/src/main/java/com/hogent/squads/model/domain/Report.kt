package com.hogent.squads.model.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.time.LocalDateTime

@Entity(tableName = "Reports")
data class Report(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "reportId")
    var reportId: Int,
    @ColumnInfo(name = "userFK")
    val userId: Int,
    @ColumnInfo(name = "date")
    val date: LocalDateTime,
    @ColumnInfo(name = "weight")
    val weight: Double,
    @ColumnInfo(name = "fatPercentage")
    val fatPercentage: Double,
    @ColumnInfo(name = "waistSize")
    val waistSize: Double,
    @ColumnInfo(name = "musclePercentage")
    val musclePercentage: Double
)

class TypeConverterReport {

    private val gson: Gson = Gson()

    @TypeConverter
    fun jsonToReport(json: String?): Report? {
        if (json == null) return null
        val listType: Type = object : TypeToken<Session>() {}.type
        return gson.fromJson<Report>(json, listType)
    }

    @TypeConverter
    fun sessionToJson(reportData: Report): String? {
        return gson.toJson(reportData)
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