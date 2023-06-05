package com.rondi.bagiapp.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class ProfileResponse(

	@field:SerializedName("profile")
	val profile: Profile? = null,

	@field:SerializedName("error")
	val error: Boolean? = null
)

@Parcelize
data class Profile(

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
) : Parcelable
