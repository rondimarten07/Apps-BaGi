package com.rondi.bagiapp.data.repository

import com.rondi.bagiapp.data.datasource.ProfileDataSource
import com.rondi.bagiapp.data.remote.ApiResponse
import com.rondi.bagiapp.data.remote.response.ProfileResponse
import com.rondi.bagiapp.data.remote.response.UpdateProfileResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val profileDataSource: ProfileDataSource
) {

    suspend fun getProfile(token: String): Flow<ApiResponse<ProfileResponse>> =
        profileDataSource.getProfile(token).flowOn(Dispatchers.IO)

    suspend fun updateProfile(
        token: String,
        file: MultipartBody.Part,
        nama: RequestBody,
        username: RequestBody,
        phone: RequestBody,
        loc: RequestBody
    ): Flow<ApiResponse<UpdateProfileResponse>> {
        return profileDataSource.updateProfile(token, file, nama, username, phone, loc)
    }
}