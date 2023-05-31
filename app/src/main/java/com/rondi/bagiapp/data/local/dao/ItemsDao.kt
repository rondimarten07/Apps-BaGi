package com.rondi.bagiapp.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rondi.bagiapp.data.local.entity.ItemsEntity

@Dao
interface ItemsDao {

    @Query("SELECT * FROM tabel_items")
    fun getAllItems(): PagingSource<Int, ItemsEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertItems(vararg: ItemsEntity)

    @Query("DELETE FROM tabel_items")
    fun deleteAllItems()

}