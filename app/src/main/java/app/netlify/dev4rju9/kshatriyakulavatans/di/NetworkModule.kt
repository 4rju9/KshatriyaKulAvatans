package app.netlify.dev4rju9.kshatriyakulavatans.di

import android.content.Context
import app.netlify.dev4rju9.kshatriyakulavatans.R
import app.netlify.dev4rju9.kshatriyakulavatans.data.remote.retrofit.CloudFlare
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(
        @ApplicationContext
        context: Context
    ): Retrofit = Retrofit.Builder()
        .baseUrl(context.getString(R.string.base)) // make sure it ends with '/'
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideVersionApi(retrofit: Retrofit): CloudFlare =
        retrofit.create(CloudFlare::class.java)
}