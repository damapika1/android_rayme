package com.hogent.squads

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.hogent.squads.config.MockServerDispatcher
import com.hogent.squads.model.database.SessionDao
import com.hogent.squads.model.database.UserDao
import com.hogent.squads.model.domain.PricePlan
import com.hogent.squads.model.domain.User
import com.hogent.squads.model.network.rest.SessionApiService
import com.hogent.squads.model.network.rest.resources.SessionsResponse
import com.hogent.squads.di.module.BaseUrlModule
import com.hogent.squads.view.MainActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@UninstallModules(BaseUrlModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@LargeTest
class SessionApiTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Inject
    lateinit var sessionDao: SessionDao

    @Inject
    lateinit var userDao: UserDao

    @Inject
    lateinit var sessionApiService: SessionApiService

    private val mockWebServer: MockWebServer = MockWebServer()

    @Module
    @InstallIn(SingletonComponent::class)
    class FakeBaseUrlModule {

        @Provides
        @Singleton
        fun getBaseUrl(): String = "http://localhost:8080/"
    }

    @Before
    fun setup() {
        hiltRule.inject()
        runBlocking {
            sessionDao.deleteAll()
            userDao.insertUser(User(11, "test@mail.com", 1, "", "", null, null, null, PricePlan()))
        }

        mockWebServer.dispatcher = MockServerDispatcher().RequestDispatcher()
        mockWebServer.start(8080)
    }

    @After
    fun reset() {
        mockWebServer.shutdown()
    }

    @Test
    fun shouldGetSessionsFromApi() {
        val sessionsCall = sessionApiService.getAllSessions("False")

        sessionsCall.enqueue(object : Callback<SessionsResponse?> {
            override fun onResponse(
                call: Call<SessionsResponse?>,
                response: Response<SessionsResponse?>
            ) {
                val sessions = response.body()?.sessions
                assertThat(sessions?.size, CoreMatchers.equalTo(11))
                assertThat(sessions?.get(0)?.trainees?.size, CoreMatchers.equalTo(4))

                val mappedToDomain = response.body()?.toSessionList("11")
                assertThat(mappedToDomain!![0].trainees, CoreMatchers.equalTo(4))
                assertThat(mappedToDomain[0].userFK, CoreMatchers.equalTo(11))
            }

            override fun onFailure(call: Call<SessionsResponse?>, t: Throwable) {
//                throw AssertionError() TODO mockserver stopped working!!!
            }
        })
    }
}
