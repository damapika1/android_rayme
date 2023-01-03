package com.hogent.squads.model.network.rest.resources

import androidx.room.ColumnInfo
import com.google.gson.annotations.SerializedName
import com.hogent.squads.model.domain.Session
import java.time.LocalDateTime
import kotlin.streams.toList

data class SessionsResponse(
    @SerializedName("sessions")
    val sessions: List<SessionsData>
) {
    fun toSessionList(currentUserId: String?): List<Session> {
        return sessions.stream()
            .map { session ->
                var userFK: Int? = null
                if (currentUserId != null) {
                    if (session.trainees?.stream()?.anyMatch { it.traineeId == currentUserId.toInt() } == true) {
                        userFK = currentUserId.toInt()
                    }
                }

                Session(
                    session.id,
                    session.title,
                    session.description,
                    LocalDateTime.parse(session.startsAt),
                    LocalDateTime.parse(session.endsAt),
                    if(session.workoutType==0) "Yoga" else "Workout",
                    session.trainees?.size ?: 0,
                    userFK
                )
            }
            .toList()
    }
}

data class SessionsData(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @ColumnInfo(name = "description")
    val description: String,
    @SerializedName("startsAt")
    val startsAt: String,
    @SerializedName("endsAt")
    val endsAt: String,
    @SerializedName("workoutType")
    val workoutType: Int,
    @SerializedName("trainees")
    val trainees: List<TraineesData>?,
    @SerializedName("totalAmount")
    val totalAmount: Int,
)

data class TraineesData(
    @SerializedName("traineeId", alternate = ["id"])
    val traineeId: Int
)
