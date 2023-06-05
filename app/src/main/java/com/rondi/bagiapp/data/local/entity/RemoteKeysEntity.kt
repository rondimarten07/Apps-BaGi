package com.rondi.bagiapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabel_remote_keys")
data class RemoteKeysEntity(
    @PrimaryKey
    val id: Int,
    val prevKey: Int?,
    val nextKey: Int?
)