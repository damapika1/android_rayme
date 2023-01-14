package com.hogent.squads

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.hogent.squads.model.database.ReportDao
import com.hogent.squads.model.database.SquadsDatabase
import com.hogent.squads.model.domain.Report
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
class ReportDatabaseTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var reportDao: ReportDao
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
        reportDao = db.reportDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetReports() {
        val report1 = Report(
            1,
            11,
            LocalDateTime.now(),
            80.0,
            0.0,
            0.0,
            0.0
        )
        runBlocking {

            reportDao.insert(report1)
            val reportResult = reportDao.getReportByUserId(11)

            assertEquals(report1, reportResult)
        }


    }
}

