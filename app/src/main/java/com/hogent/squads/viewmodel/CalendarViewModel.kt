package com.hogent.squads.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hogent.squads.utils.TokenSessionManager
import com.hogent.squads.model.repository.SessionRepository
import com.hogent.squads.model.repository.UserRepository
import com.hogent.squads.model.domain.Session
import com.hogent.squads.model.network.rest.resources.JoinRequest
import com.hogent.squads.model.network.rest.resources.JoinSessionResponse
import com.hogent.squads.model.network.rest.resources.SessionsResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val userRepository: UserRepository,
    private val tokenSessionManager: TokenSessionManager
) : ViewModel() {

    val allSessions: LiveData<List<Session>>
        get() = sessionRepository.getSessions()

    val userSessions: LiveData<List<Session>>
        get() = sessionRepository.getJoinedSessions()

    private val _loading: MutableLiveData<Boolean> = MutableLiveData(false)
    val loading:LiveData<Boolean>
        get() = _loading

    fun updateSessions() {
        fetchSessions()
    }

    fun setLoading() {
        _loading.value = true
    }

    private fun fetchSessions() {
        sessionRepository.getSessionsFromApi().enqueue(object : Callback<SessionsResponse?> {
            override fun onResponse(
                call: Call<SessionsResponse?>,
                response: Response<SessionsResponse?>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    viewModelScope.launch {
                        val sessions = response.body()!!.toSessionList(getCurrentUserId())
                        sessionRepository.saveSessions(sessions)
                        sessionRepository.deleteOld()
                    }
                } else if (response.code() == 401) {
                    viewModelScope.launch {
                        userRepository.deleteCurrentUser();
                    }
                }

                _loading.value = false
            }

            override fun onFailure(call: Call<SessionsResponse?>, error: Throwable) {
                Timber.i(error)
                _loading.value = false
            }
        })
    }

    fun getSessionLiveData(sessionId: Int): LiveData<Session> {
        return sessionRepository.getSessionLiveData(sessionId)
    }

    fun joinSession(joinRequest: JoinRequest) {
        sessionRepository.joinSession(joinRequest)
            .enqueue(object : Callback<JoinSessionResponse?> {
                override fun onResponse(
                    call: Call<JoinSessionResponse?>,
                    response: Response<JoinSessionResponse?>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        viewModelScope.launch {
                            val session = sessionRepository.getSessionById(joinRequest.sessionId)
                            session.joinSession(getCurrentUserId()!!)
                            sessionRepository.saveSession(session)
                        }
                    }
                }

                override fun onFailure(call: Call<JoinSessionResponse?>, error: Throwable) {
                    Timber.i(error)
                }
            })
    }

    fun leaveSession(joinRequest: JoinRequest) {
        sessionRepository.leaveSession(joinRequest)
            .enqueue(object : Callback<JoinSessionResponse?> {
                override fun onResponse(
                    call: Call<JoinSessionResponse?>,
                    response: Response<JoinSessionResponse?>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        viewModelScope.launch {
                            val session = sessionRepository.getSessionById(joinRequest.sessionId)
                            session.leaveSession()
                            sessionRepository.saveSession(session)
                        }
                    }
                }

                override fun onFailure(call: Call<JoinSessionResponse?>, error: Throwable) {
                    Timber.i(error)
                }
            })
    }

    fun getCurrentUserId(): String? {
        return tokenSessionManager.fetchUserId()
    }

    suspend fun deleteAll() {
        sessionRepository.deleteAll()
    }

    override fun onCleared() {
        Timber.tag("CALENDAR_VIEW_MODEL").d("Clearing the viewmodel...")
        super.onCleared()
    }
}
