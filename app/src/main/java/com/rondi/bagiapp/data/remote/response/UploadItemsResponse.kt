package com.rondi.bagiapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class UploadItemsResponse(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)
