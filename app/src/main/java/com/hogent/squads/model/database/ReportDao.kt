package com.hogent.squads.model.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.hogent.squads.model.domain.Report

@Dao
interface ReportDao {

    @Query("SELECT * FROM Reports Where userFK=:userId ORDER BY Date DESC")
    fun getReportsByUserId(userId: Int?): LiveData<List<Report>>

    @Query("SELECT * FROM Reports Where userFK=:userId LIMIT 1")
    suspend fun getReportByUserId(userId: Int?): Report

    @Delete
    suspend fun delete(report: Report)

    @Update
    suspend fun update(report: Report)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(report: Report)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertData(data: List<Report>)

    @Query("DELETE FROM Reports")
    suspend fun deleteAll()

}

