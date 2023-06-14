package com.rondi.bagiapp.ui.myitem

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import com.rondi.bagiapp.data.remote.ApiResponse
import com.rondi.bagiapp.data.remote.response.MyItemResponse
import com.rondi.bagiapp.data.repository.AuthRepository
import com.rondi.bagiapp.data.repository.ItemsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyItemViewModel @Inject constructor(
    private val itemsRepository: ItemsRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    fun getMyItems(token: String, userId: String) : LiveData<ApiResponse<MyItemResponse>> {
        val result = MutableLiveData<ApiResponse<MyItemResponse>>()
        viewModelScope.launch {
            itemsRepository.getMyItem(token, userId).collect {
                result.postValue(it)
            }
        }
        return result
    }

    fun getAuthToken(): Flow<String?> = authRepository.getAuthToken()

    fun getAuthUserId(): Flow<String?> = authRepository.getUserId()
}