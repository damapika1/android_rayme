package com.hogent.squads

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.hogent.squads.model.database.ReportDao
import com.hogent.squads.model.database.SquadsDatabase
import com.hogent.squads.model.domain.Report
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
    @OptIn(DelicateCoroutinesApi::class)
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

        val report2 = Report(
            17,
            11,
            LocalDateTime.now(),
            0.0,
            20.0,
            100.0,
            25.0
        )

        GlobalScope.launch {
            reportDao.insertData(listOf(report1, report2))
        }

        val reportResult = reportDao.getReportsByUserId(11).value
        assertEquals(report1, reportResult?.find { it.weight == 80.0 })
        assertEquals(report2, reportResult?.find { it.reportId == 17 })

    }
}
