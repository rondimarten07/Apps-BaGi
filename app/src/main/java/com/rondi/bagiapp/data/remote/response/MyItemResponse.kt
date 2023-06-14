package com.rondi.bagiapp.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class MyItemResponse(

	@field:SerializedName("items")
	val item: List<MyItem?>? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

@Parcelize
data class MyItem(

	@field:SerializedName("loc")
	val loc: String? = null,

	@field:SerializedName("user_id")
	val userId: Int,

	@field:SerializedName("photoUrl")
	val photoUrl: String? = null,

	@field:SerializedName("photoItems")
	val photoItems: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("kategori")
	val kategori: String? = null,

	@field:SerializedName("nohp")
	val nohp: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("createAt")
	val createAt: String? = null
) : Parcelable
