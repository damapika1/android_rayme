package com.hogent.squads.model.repository

import androidx.lifecycle.LiveData
import com.hogent.squads.model.database.SessionDao
import com.hogent.squads.model.domain.Session
import com.hogent.squads.model.network.rest.SessionApiService
import com.hogent.squads.model.network.rest.resources.JoinRequest
import com.hogent.squads.model.network.rest.resources.JoinSessionResponse
import com.hogent.squads.model.network.rest.resources.SessionsResponse
import retrofit2.Call
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepository @Inject constructor(
    private val sessionApiService: SessionApiService,
    private val sessionDao: SessionDao
) {

    fun getSessionsFromApi(): Call<SessionsResponse> {
        return sessionApiService.getAllSessions("False")
    }

    fun joinSession(joinRequest: JoinRequest): Call<JoinSessionResponse> {
        return sessionApiService.joinSession(joinRequest)
    }

    fun leaveSession(joinRequest: JoinRequest): Call<JoinSessionResponse> {
        return sessionApiService.leaveSession(joinRequest)
    }

    fun getSessions(): LiveData<List<Session>> {
        return sessionDao.getAllSessions()
    }

    suspend fun saveSessions(sessions: List<Session>) {
        sessionDao.insertData(sessions)
    }

    fun saveSession(session: Session){
        sessionDao.insert(session)
    }

    suspend fun getSessionById(id: Int): Session {
        return sessionDao.getSessionById(id)
    }

    fun getSessionLiveData(sessionId: Int): LiveData<Session> {
        return sessionDao.getSessionLiveDataById(sessionId)
    }

    fun getJoinedSessions(): LiveData<List<Session>> {
        return sessionDao.getJoinedSessions()
    }

    suspend fun deleteAll() {
        sessionDao.deleteAll()
    }

    suspend fun deleteOld() {
        sessionDao.deleteBeforeDate(LocalDateTime.now())
    }
}
