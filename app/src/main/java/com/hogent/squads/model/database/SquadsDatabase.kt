package com.hogent.squads.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hogent.squads.model.domain.*


@Database(
    entities = [User::class, Session::class, Report::class],
    version = 19,
    exportSchema = false
)
@TypeConverters(TypeConverterUser::class,TypeConverterSession::class)
abstract class SquadsDatabase : RoomDatabase() {

    abstract val userDao: UserDao
    abstract val sessionDao: SessionDao
    abstract val reportDao: ReportDao

    companion object {
        private const val DB_NAME = "squads"
        @Volatile
        private var INSTANCE: SquadsDatabase? = null

        fun getInstance(context: Context): SquadsDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                SquadsDatabase::class.java, DB_NAME
            ).allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
    }
}

