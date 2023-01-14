package com.hogent.squads.viewmodel

import android.widget.ScrollView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.hogent.squads.model.repository.UserRepository
import com.hogent.squads.model.domain.Subscription
import com.hogent.squads.model.domain.User
import com.hogent.squads.model.network.rest.resources.LoginRequest
import com.hogent.squads.model.network.rest.resources.LoginResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    val activeUser : LiveData<User?>
        get() = userRepository.activeUser

    val subscription : LiveData<Subscription?>
        get() = userRepository.subscriptions

    val turns : LiveData<Int>
        get() = userRepository.turnsAvailable

    private val _editStatus = MutableLiveData("")
    val editStatus:LiveData<String>
        get() = _editStatus

    var staleUser = true

    fun login(loginRequest: LoginRequest, loginScrollView: ScrollView) {
        userRepository.attemptLogin(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!.mapToUser()
                    staleUser = false
                    viewModelScope.launch {
                        userRepository.loginSuccessful(user, response.body()!!.token)
                    }
                } else {
                    Snackbar.make(
                        loginScrollView,
                        "Inloggen mislukt, juiste gegevens?",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Snackbar.make(
                    loginScrollView,
                    "Geen internet connectie of server staat uit",
                    Snackbar.LENGTH_LONG
                ).show()
                Timber.tag("logging").d("Could not login user")
            }
        })
    }

    fun getUserInfo(user: User) {
        viewModelScope.launch {
            if(staleUser) {
                userRepository.updateUserInfo(user)
                staleUser = false
            }

            userRepository.fetchUserSubscriptions(user.userId)
            userRepository.fetchUserTurncards(user.userId)
        }
    }

    fun deleteCurrentUser() {
        viewModelScope.launch {
            userRepository.deleteCurrentUser()
        }
    }

    fun extendSubscription(user:User) {
        userRepository.extendSubscription(user)
    }

    fun increaseTurns(user:User) {
        userRepository.increaseTurns(user)
    }

    fun editUser(updatedUser:User) {
        _editStatus.value = "loading"
        viewModelScope.launch {
            userRepository.editUser(updatedUser)
            _editStatus.value = "updated"
        }
    }

    fun resetEditStatus() {
        _editStatus.value = ""
    }

    override fun onCleared() {
        Timber.tag("USER_VIEW_MODEL").d("Clearing the viewmodel...")
        super.onCleared()
    }
}
