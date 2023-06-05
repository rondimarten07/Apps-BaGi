package com.rondi.bagiapp.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "tabel_items")
@Parcelize
data class ItemsEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "photoUrl")
    val photoUrl: String,

    @ColumnInfo(name = "createAt")
    val createAt: String,

    @ColumnInfo(name = "photo_items")
    val photoItems : List<String>,

    @ColumnInfo(name = "nohp")
    val nohp: String,

    @ColumnInfo(name = "loc")
    val loc: String,

    @ColumnInfo(name = "kategori")
    val kategori: String
) : Parcelable
