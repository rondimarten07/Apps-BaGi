package com.rondi.bagiapp.ui.upload

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rondi.bagiapp.data.remote.ApiResponse
import com.rondi.bagiapp.data.remote.response.UploadItemsResponse
import com.rondi.bagiapp.data.repository.AuthRepository
import com.rondi.bagiapp.data.repository.ItemsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    fun getAuthToken(): Flow<String?> = authRepository.getAuthToken()

     fun uploadItems(
        token: String,
        file: MultipartBody.Part,
        title: RequestBody,
        description: RequestBody,
        category: RequestBody
    ): MutableLiveData<ApiResponse<UploadItemsResponse>> {
        val result = MutableLiveData<ApiResponse<UploadItemsResponse>>()
        viewModelScope.launch {
            itemsRepository.uploadItems(token, file, title, description, category).collect {
                result.postValue(it)
            }
        }
        return result
    }

}