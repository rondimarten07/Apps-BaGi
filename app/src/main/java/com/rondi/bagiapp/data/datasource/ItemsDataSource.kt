package com.rondi.bagiapp.data.datasource


import com.rondi.bagiapp.data.remote.ApiResponse
import com.rondi.bagiapp.data.remote.response.*
import com.rondi.bagiapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItemsDataSource @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getAllItem(
        token: String,
    ): Flow<ApiResponse<ItemsResponse>> {
        return flow {
            try {
                emit(ApiResponse.Loading)
                val response = apiService.getAllItem(token)
                if (!response.error) {
                    emit(ApiResponse.Success(response))
                }
            } catch (ex: Exception) {
                emit(ApiResponse.Error(ex.message.toString()))
            }
        }
    }

    suspend fun uploadItems(
        token: String,
        file: MultipartBody.Part,
        title: RequestBody,
        description: RequestBody,
        category: RequestBody
    ): Flow<ApiResponse<UploadItemsResponse>> {
        return flow {
            try {
                emit(ApiResponse.Loading)
                val response = apiService.uploadItems(token, file, title, description, category)
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

    suspend fun getMyItem(
        token: String,
        userId : String
    ): Flow<ApiResponse<MyItemResponse>> {
        return flow {
            try {
                emit(ApiResponse.Loading)
                val response = apiService.getMyItem(token, userId)
                if (response.error != true) {
                    emit(ApiResponse.Success(response))
                }
            } catch (ex: Exception) {
                emit(ApiResponse.Error(ex.message.toString()))
            }
        }
    }

    suspend fun searchItem(
        token: String,
        keyword: String
    ): Flow<ApiResponse<SearchResponse>> {
        return flow {
            try {
                emit(ApiResponse.Loading)
                val response = apiService.searchItems(token, keyword)
                if (!response.error) {
                    emit(ApiResponse.Success(response))
                }
            } catch (ex: Exception) {
                emit(ApiResponse.Error(ex.message.toString()))
            }
        }
    }
}