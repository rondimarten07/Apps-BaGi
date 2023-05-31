package com.rondi.bagiapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class ItemsResponse(

	@field:SerializedName("listItems")
	val listItems: List<ListItemsItem>,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class ListItemsItem(

	@field:SerializedName("photoUrl")
	val photoUrl: String,

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("loc")
	val loc: String,

	@field:SerializedName("photoItems")
	val photoItems: List<String>,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("nohp")
	val nohp: String,

	@field:SerializedName("kategori")
	val kategori: String,

	@field:SerializedName("id")
	val id: String
)
