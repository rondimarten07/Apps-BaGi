package com.rondi.bagiapp.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class ItemsResponse(

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("items")
	val items: List<ItemsItem>
)

@Parcelize
data class ItemsItem(

	@field:SerializedName("loc")
	val loc: String,

	@field:SerializedName("user_id")
	val userId: Int,

	@field:SerializedName("photoUrl")
	val photoUrl: String,

	@field:SerializedName("photoItems")
	val photoItems: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("kategori")
	val kategori: String,

	@field:SerializedName("nohp")
	val nohp: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("createAt")
	val createAt: String
) : Parcelable
