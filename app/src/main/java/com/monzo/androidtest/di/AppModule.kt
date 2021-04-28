package com.monzo.androidtest.di

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.monzo.androidtest.R
import com.monzo.androidtest.api.GuardianService
import com.monzo.androidtest.database.ArticleDatabase
import com.monzo.androidtest.ui.articledetail.ArticleDetailViewModel
import com.monzo.androidtest.ui.articlelists.ArticlesViewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.multibindings.IntoMap
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    private const val BASE_URL = "https://content.guardianapis.com"
    private const val HEADER_API_KEY = "api-key"

    @Provides
    @Singleton
    fun provideDatabase(app: Application) =
            Room.databaseBuilder(app, ArticleDatabase::class.java, "article_database")
                    .fallbackToDestructiveMigration()
                    .build()

    @Provides
    fun provideTaskDao(database: ArticleDatabase) = database.articleDao()

    @Provides
    fun provideMoshi() = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .add(Date::class.java, Rfc3339DateJsonAdapter())
            .build()

    @Provides
    @Singleton
    fun provideGuardianService(okHttpClient: OkHttpClient, moshi: Moshi) =
            Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(MoshiConverterFactory.create(moshi))
                    .client(okHttpClient)
                    .addCallAdapterFactory(CoroutineCallAdapterFactory())
                    .build()
                    .create(GuardianService::class.java)

    @Provides
    fun provideAuthInterceptor(app: Application) =
            object : Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    val original = chain.request()
                    val hb = original.headers.newBuilder()
                    hb.add(HEADER_API_KEY, app.resources.getString(R.string.guardian_api_key))
                    return chain.proceed(original.newBuilder().headers(hb.build()).build())
                }
            }

    @Provides
    fun provideOkHttpClient(interceptor: Interceptor): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val clientBuilder = OkHttpClient.Builder()
        clientBuilder.addInterceptor(interceptor)
        clientBuilder.addInterceptor(loggingInterceptor)
        return clientBuilder.build()
    }

}
