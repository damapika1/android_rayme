package com.hogent.squads

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.hogent.squads.model.database.SessionDao
import com.hogent.squads.model.database.SquadsDatabase
import com.hogent.squads.model.domain.Session
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class SessionDatabaseTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var sessionDao: SessionDao
    private lateinit var db: SquadsDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, SquadsDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        sessionDao = db.sessionDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetSession() {
        val session = Session(
            1,
            "titel",
            "descrp",
            LocalDateTime.now(),
            LocalDateTime.now(),
            "YOGA",
            0,
            null
        )
        sessionDao.insert(session)
        val sessionResult = sessionDao.getSession()
        assertEquals("titel", sessionResult?.title)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetSessionById() {
        val session = Session(
            1,
            "titel",
            "descrp",
            LocalDateTime.now(),
            LocalDateTime.now(),
            "YOGA",
            0,
            null
        )

        session.joinSession("11")

        sessionDao.insert(session)
        var sessionResult: LiveData<Session>
        runBlocking {
            sessionResult = sessionDao.getSessionLiveDataById(1)
            sessionResult.observeForever {
                assertEquals("titel", it.title)
                assertEquals(1, it.trainees)
                assertEquals(11, it.userFK)
            }
        }
    }
}
