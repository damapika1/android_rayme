package com.hogent.squads.model.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.hogent.squads.model.domain.User

@Dao
interface UserDao {

    @Query("SELECT * FROM User LIMIT 1")
    fun getUser(): LiveData<User?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateUser(user: User)

    @Query("DELETE FROM User")
    suspend fun deleteAll()
}
