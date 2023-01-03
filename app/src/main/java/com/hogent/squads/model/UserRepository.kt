package com.hogent.squads.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hogent.squads.TokenSessionManager
import com.hogent.squads.model.database.UserDao
import com.hogent.squads.model.domain.Subscription
import com.hogent.squads.model.domain.User
import com.hogent.squads.model.network.rest.SubscriptionApiService
import com.hogent.squads.model.network.rest.TurncardApiService
import com.hogent.squads.model.network.rest.UserApiService
import com.hogent.squads.model.network.rest.resources.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.net.ConnectException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.jvm.optionals.getOrDefault

@Singleton
class UserRepository @Inject constructor(
    private val tokenSessionManager: TokenSessionManager,
    private val userApiService: UserApiService,
    private val userDao: UserDao,
    private val subscriptionService: SubscriptionApiService,
    private val turncardService: TurncardApiService
) {
    val activeUser : LiveData<User?>
        get() = userDao.getUser()

    private val _subscriptions = MutableLiveData<Subscription?>()
    val subscriptions
        get() = _subscriptions

    private val _turnsAvailable = MutableLiveData(0)
    val turnsAvailable
        get() = _turnsAvailable

    fun attemptLogin(loginRequest: LoginRequest): Call<LoginResponse> {
        return userApiService.login(loginRequest)
    }

    suspend fun loginSuccessful(user: User, userToken: String) {
        userDao.insertUser(user)
        tokenSessionManager.saveUserId(user.userId.toString())
        tokenSessionManager.saveAuthToken(userToken)
    }

    suspend fun updateUserInfo(user: User) {
        try {
            val remoteUser = userApiService.getUserInfo(user.userId).mapToUser()
            Timber.tag("USER_REPO").d("Price plan: ${remoteUser.pricePlan}")
            userDao.updateUser(remoteUser)
        } catch (e: ConnectException) {
            Timber.tag("USER_REPO").e(e)
        }
    }

    suspend fun editUser(updatedUser:User) {
        val fields = EditUserDto(
            updatedUser.email,
            updatedUser.firstName!!,
            updatedUser.lastName!!,
            updatedUser.address!!,
            updatedUser.phoneNumber!!
        )

        val req = EditUserRequest(
            updatedUser.userId,
            fields
        )
        try {
            userApiService.editUser(req)
        } catch (e: ConnectException) {
            Timber.tag("USER_REPO").e(e)
        }
        updateUserInfo(updatedUser)
    }

    fun fetchUserSubscriptions(userId:Int) {
        subscriptionService.getSubscriptionsForUser(userId)
            .enqueue(object: Callback<SubscriptionResponse> {
                override fun onResponse(
                    call: Call<SubscriptionResponse>,
                    response: Response<SubscriptionResponse>
                ) {
                    if(response.isSuccessful) {
                        val sub =
                            response.body()!!.toSubList().maxByOrNull { s -> s.validTill }
                        _subscriptions.value = sub
                    } else {
                        Timber.tag("USER_REPO").d("Res: %s", response.message())
                    }
                }

                override fun onFailure(call: Call<SubscriptionResponse>, t: Throwable) {
                    Timber.tag("USER_REPO").e(t)
                }
            })
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun fetchUserTurncards(userId: Int) {
        turncardService.getTurncardsForUser(userId)
            .enqueue(object: Callback<TurncardResponse> {
                override fun onResponse(
                    call: Call<TurncardResponse>,
                    response: Response<TurncardResponse>
                ) {
                    if(response.isSuccessful) {
                        val turns = response.body()!!.toTurncardList().stream()
                            .map { it.numberOfTurns }
                            .reduce { turns, vars -> turns + vars}
                            .getOrDefault(0)

                        turnsAvailable.value = turns
                    } else {
                        Timber.tag("USER_REPO").d("Res: %s", response.message())
                    }
                }

                override fun onFailure(call: Call<TurncardResponse>, t: Throwable) {
                    Timber.tag("USER_REPO").e(t)
                }
            })
    }

    fun extendSubscription(user: User) {
        val dto = ExtendSubscriptionDTO(user.userId)
        val req = ExtendSubscriptionRequest(dto)
        subscriptionService.extendUserSubscription(req)
            .enqueue(object : Callback<SubscriptionDetailResponse> {
                override fun onResponse(
                    call: Call<SubscriptionDetailResponse>,
                    response: Response<SubscriptionDetailResponse>
                ) {
                    if(!response.isSuccessful) {
                        Timber.tag("USER_REPO").d("Res: $response")
                    } else {
                        fetchUserSubscriptions(user.userId)
                    }
                }

                override fun onFailure(call: Call<SubscriptionDetailResponse>, t: Throwable) {
                    Timber.tag("USER_REPO").e(t)
                }
            })
    }

    fun increaseTurns(user: User) {
        val dto = IncreaseTurnsDTO(user.userId)
        val req = IncreaseTurnsRequest(dto)
        turncardService.increaseTurnsForUser(req)
            .enqueue(object:Callback<TurncardDetailResponse> {
                override fun onResponse(
                    call: Call<TurncardDetailResponse>,
                    response: Response<TurncardDetailResponse>
                ) {
                    if(response.isSuccessful) {
                        fetchUserTurncards(user.userId)
                    } else {
                        Timber.tag("USER_REPO").d("Response: $response")
                    }
                }

                override fun onFailure(call: Call<TurncardDetailResponse>, t: Throwable) {
                    Timber.tag("USER_REPO").e(t)
                }
            })
    }

    suspend fun deleteCurrentUser() {
        userDao.deleteAll()
        tokenSessionManager.deleteAuthToken()
        tokenSessionManager.deleteAuthId()
    }
}
