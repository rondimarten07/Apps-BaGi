package com.rondi.bagiapp.data.repository

import com.rondi.bagiapp.data.datasource.ItemsDataSource
import com.rondi.bagiapp.data.remote.ApiResponse
import com.rondi.bagiapp.data.remote.response.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ItemsRepository @Inject constructor(private val itemsDataSource: ItemsDataSource) {
    suspend fun getAllItem(token: String): Flow<ApiResponse<ItemsResponse>> =
        itemsDataSource.getAllItem(token).flowOn(Dispatchers.IO)

    suspend fun uploadItems(
        token: String,
        file: MultipartBody.Part,
        title: RequestBody,
        description: RequestBody,
        category: RequestBody
    ): Flow<ApiResponse<UploadItemsResponse>> {
        return itemsDataSource.uploadItems(token, file, title, description, category)
    }

    suspend fun getMyItem(token: String, userId: String): Flow<ApiResponse<MyItemResponse>> =
        itemsDataSource.getMyItem(token, userId).flowOn(Dispatchers.IO)

    suspend fun searchItem(token: String, keyword: String): Flow<ApiResponse<SearchResponse>> =
        itemsDataSource.searchItem(token, keyword).flowOn(Dispatchers.IO)
}