package com.rondi.bagiapp.di

import android.content.Context
import androidx.room.Room
import com.rondi.bagiapp.data.local.dao.ItemsDao
import com.rondi.bagiapp.data.local.dao.RemoteKeysDao
import com.rondi.bagiapp.data.local.db.ItemsDatabase
import com.rondi.bagiapp.data.remote.retrofit.ApiConfig
import com.rondi.bagiapp.data.remote.retrofit.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Provides
    @Singleton
    fun provideApiService(): ApiService = ApiConfig.getApiService()

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): ItemsDatabase {
        return Room.databaseBuilder(context, ItemsDatabase::class.java, "Bagi.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideItemsDao(database: ItemsDatabase): ItemsDao = database.ItemsDao()

    @Provides
    fun provideRemoteKeyDao(database: ItemsDatabase): RemoteKeysDao = database.getRemoteKeysDao()
}