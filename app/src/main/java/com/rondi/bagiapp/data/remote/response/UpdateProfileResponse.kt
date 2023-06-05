package com.rondi.bagiapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class UpdateProfileResponse(

	@field:SerializedName("profile")
	val profile: EditProfile? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class EditProfile(

	@field:SerializedName("loc")
	val loc: String? = null,

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("phone")
	val phone: String? = null,

	@field:SerializedName("avatar")
	val avatar: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("username")
	val username: String? = null
)
