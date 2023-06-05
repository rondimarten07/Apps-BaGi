package com.rondi.bagiapp.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rondi.bagiapp.data.local.dao.ItemsDao
import com.rondi.bagiapp.data.local.dao.RemoteKeysDao
import com.rondi.bagiapp.data.local.entity.ItemsEntity
import com.rondi.bagiapp.data.local.entity.ListStringConverter
import com.rondi.bagiapp.data.local.entity.RemoteKeysEntity

@Database(entities = [ItemsEntity::class, RemoteKeysEntity::class], version = 1, exportSchema = false)
@TypeConverters(ListStringConverter::class)
abstract class ItemsDatabase : RoomDatabase() {

    abstract fun ItemsDao(): ItemsDao

    abstract fun getRemoteKeysDao(): RemoteKeysDao


}