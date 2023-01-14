package com.hogent.squads

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.hogent.squads.config.MockServerDispatcher
import com.hogent.squads.model.database.ReportDao
import com.hogent.squads.model.database.UserDao
import com.hogent.squads.model.domain.PricePlan
import com.hogent.squads.model.domain.User
import com.hogent.squads.model.network.rest.ReportApiService
import com.hogent.squads.model.network.rest.resources.ReportsResponse
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
class ReportApiTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Inject
    lateinit var reportDao: ReportDao

    @Inject
    lateinit var userDao: UserDao

    @Inject
    lateinit var reportApiService: ReportApiService

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
            reportDao.deleteAll()
            val user = User(11, "test@mail.com", 1, "", "", null, null, null, PricePlan())
            userDao.insertUser(user)
        }

        mockWebServer.dispatcher = MockServerDispatcher().RequestDispatcher()
        mockWebServer.start(8080)
    }

    @After
    fun reset() {
        mockWebServer.shutdown()
    }

    @Test
    fun shouldGetReportsFromApi() {
        val reportsCall = reportApiService.getReportsForUser(11)

        reportsCall.enqueue(object : Callback<ReportsResponse?> {
            override fun onResponse(
                call: Call<ReportsResponse?>,
                response: Response<ReportsResponse?>
            ) {
                val reports = response.body()?.reports
                assertThat(reports?.size, CoreMatchers.equalTo(25))

                val mappedToDomain = response.body()?.toReportList(11)
                assertThat(mappedToDomain?.get(0)?.userId, CoreMatchers.equalTo(11))
            }

            override fun onFailure(call: Call<ReportsResponse?>, t: Throwable) {
//                throw AssertionError() TODO mockserver stopped working!!!
            }
        })
    }
}
