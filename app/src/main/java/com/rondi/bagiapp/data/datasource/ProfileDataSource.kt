package com.rondi.bagiapp.data.datasource

import com.rondi.bagiapp.data.local.db.ItemsDatabase
import com.rondi.bagiapp.data.remote.ApiResponse
import com.rondi.bagiapp.data.remote.response.ProfileResponse
import com.rondi.bagiapp.data.remote.response.UpdateProfileResponse
import com.rondi.bagiapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Part
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileDataSource @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getProfile(
        token: String
    ): Flow<ApiResponse<ProfileResponse>> {
        return flow {
            try {
                emit(ApiResponse.Loading)
                val response = apiService.getProfile(token)
                if (response.error != true) {
                    emit(ApiResponse.Success(response))
                }
            } catch (ex: Exception) {
                emit(ApiResponse.Error(ex.message.toString()))
            }
        }
    }


    suspend fun updateProfile(
        token: String,
        file: MultipartBody.Part,
        nama: RequestBody,
        username: RequestBody,
        phone: RequestBody,
        loc: RequestBody
    ): Flow<ApiResponse<UpdateProfileResponse>> {
        return flow {
            try {
                emit(ApiResponse.Loading)
                val response = apiService.updateProfile(token, file, nama, username, phone, loc)
                if (response.error != true) {
                    emit(ApiResponse.Success(response))
                } else {
                    response.message?.let { ApiResponse.Error(it) }?.let { emit(it) }
                }
            } catch (ex: Exception) {
                emit(ApiResponse.Error(ex.message.toString()))
            }
        }
    }

}