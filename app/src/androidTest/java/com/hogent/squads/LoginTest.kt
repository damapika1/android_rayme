package com.hogent.squads

import androidx.lifecycle.LiveData
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.hogent.squads.config.MockServerDispatcher
import com.hogent.squads.model.database.UserDao
import com.hogent.squads.model.domain.User
import com.hogent.squads.di.module.BaseUrlModule
import com.hogent.squads.view.MainActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import javax.inject.Singleton

@UninstallModules(BaseUrlModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

    @Inject
    lateinit var userDao: UserDao

    private lateinit var mockWebServer: MockWebServer

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
            userDao.deleteAll()
        }

        mockWebServer = MockWebServer()
        mockWebServer.dispatcher = MockServerDispatcher().RequestDispatcher()
        mockWebServer.start(8080)

//        navController.setGraph(R.id.nav_graph)
    }

    @After
    fun reset() {
        mockWebServer.shutdown()
    }

    @OptIn(DelicateCoroutinesApi::class)
    @Test
    fun shouldLoginAndSeeCalendarView() {
        Espresso.onView(withId(R.id.usernameInputField)).perform(ViewActions.typeText("user@mail.com"))
        Espresso.onView(withId(R.id.passwordInputField)).perform(ViewActions.typeText("password"))
        Espresso.onView(withId(R.id.loginButton)).perform(ViewActions.click())

        val userLiveData: LiveData<User?> = userDao.getUser()
        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                Thread.sleep(1000)
            }
            userLiveData.observeForever {
                assertThat(it?.userId, CoreMatchers.equalTo(11))
            }
        }
    }
}
