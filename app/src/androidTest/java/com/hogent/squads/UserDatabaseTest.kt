package com.hogent.squads

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hogent.squads.model.database.SquadsDatabase
import com.hogent.squads.model.database.UserDao
import com.hogent.squads.model.domain.PricePlan
import com.hogent.squads.model.domain.User
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class UserDatabaseTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var userDao: UserDao
    private lateinit var db: SquadsDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, SquadsDatabase::class.java).build()
        userDao = db.userDao
        runBlocking {
            userDao.deleteAll()
        }
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetUser() {
        runBlocking {
            val user = User(
                11,
                "user@mail.com",
                1,
                "user",
                "user",
                null,
                null,
                null,
                PricePlan()
            )
            userDao.insertUser(user)
        }

        val userLiveData: LiveData<User?> = userDao.getUser()
        userLiveData.observeForever {
            MatcherAssert.assertThat(it!!.userId, CoreMatchers.equalTo(11))
        }
    }
}
