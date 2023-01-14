package com.hogent.squads.di.module

import android.app.Application
import com.hogent.squads.model.database.ReportDao
import com.hogent.squads.model.database.SessionDao
import com.hogent.squads.model.database.SquadsDatabase
import com.hogent.squads.model.database.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun getSquadsDatabase(context: Application): SquadsDatabase {
        return SquadsDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun getUserDao(squadsDatabase: SquadsDatabase): UserDao {
        return squadsDatabase.userDao
    }

    @Provides
    @Singleton
    fun getSessionDao(squadsDatabase: SquadsDatabase): SessionDao {
        return squadsDatabase.sessionDao
    }

    @Provides
    @Singleton
    fun getReportDao(squadsDatabase: SquadsDatabase): ReportDao {
        return squadsDatabase.reportDao
    }
}
