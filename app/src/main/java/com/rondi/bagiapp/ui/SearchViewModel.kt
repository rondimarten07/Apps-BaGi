package com.rondi.bagiapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rondi.bagiapp.data.remote.ApiResponse
import com.rondi.bagiapp.data.remote.response.ItemsResponse
import com.rondi.bagiapp.data.remote.response.SearchResponse
import com.rondi.bagiapp.data.repository.AuthRepository
import com.rondi.bagiapp.data.repository.ItemsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    fun getAuthToken(): Flow<String?> = authRepository.getAuthToken()

    fun searchItem(token: String, keyword: String) : LiveData<ApiResponse<SearchResponse>> {
        val result = MutableLiveData<ApiResponse<SearchResponse>>()
        viewModelScope.launch {
            itemsRepository.searchItem(token, keyword).collect {
                result.postValue(it)
            }
        }
        return result
    }

}