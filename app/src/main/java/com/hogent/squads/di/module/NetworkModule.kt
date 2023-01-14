package com.hogent.squads.di.module

import com.hogent.squads.model.network.rest.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun createRetrofitClient(okHttpClient: OkHttpClient, baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun createUserApiService(retrofit: Retrofit): UserApiService {
        return retrofit.create(UserApiService::class.java)
    }

    @Provides
    @Singleton
    fun createSessionApiService(retrofit: Retrofit): SessionApiService {
        return retrofit.create(SessionApiService::class.java)
    }

    @Provides
    @Singleton
    fun createReportApiService(retrofit: Retrofit): ReportApiService {
        return retrofit.create(ReportApiService::class.java)
    }

    @Provides
    @Singleton
    fun createSubscritpionApiService(retrofit: Retrofit): SubscriptionApiService {
        return retrofit.create(SubscriptionApiService::class.java)
    }

    @Provides
    @Singleton
    fun createTurncardApiService(retrofit: Retrofit): TurncardApiService {
        return retrofit.create(TurncardApiService::class.java)
    }

    @Provides
    @Singleton
    fun createOkHttpClient(authInterceptor: Interceptor): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(authInterceptor)
            .readTimeout(2, TimeUnit.SECONDS)
            .connectTimeout(2, TimeUnit.SECONDS)
            .writeTimeout(2, TimeUnit.SECONDS)
            .callTimeout(2, TimeUnit.SECONDS)
            .build()
    }
}
