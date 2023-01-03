package com.hogent.squads.model.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.hogent.squads.model.domain.Session
import java.time.LocalDateTime


@Dao
interface SessionDao {

    @Query("SELECT * FROM Sessions WHERE userFK IS NOT NULL ORDER BY StartsAt ASC")
    fun getJoinedSessions(): LiveData<List<Session>>

    @Query("SELECT * FROM Sessions WHERE userFK IS NULL ORDER BY StartsAt ASC")
    fun getAllSessions(): LiveData<List<Session>>

    @Query("SELECT * FROM Sessions Where sessionId=:sessionId")
    fun getSessionLiveDataById(sessionId: Int): LiveData<Session>

    @Query("SELECT * FROM Sessions Where sessionId=:sessionId")
    suspend fun getSessionById(sessionId: Int): Session

    @Delete
    fun delete(session: Session)

    @Query("DELETE FROM Sessions WHERE EndsAt < :dateBefore")
    suspend fun deleteBeforeDate(dateBefore:LocalDateTime)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(session: Session)

    @Update
    fun update(session: Session)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertData(data: List<Session>)

    @Query("SELECT * FROM Sessions ORDER BY sessionId DESC LIMIT 1")
    fun getSession(): Session?

    @Query("DELETE FROM Sessions")
    suspend fun deleteAll()

}

