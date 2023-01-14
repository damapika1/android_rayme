package com.hogent.squads.di.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class BaseUrlModule {

    @Provides
    @Singleton
    fun getBaseUrl(): String {
        return "http://10.0.2.2:5121"
    }
}
