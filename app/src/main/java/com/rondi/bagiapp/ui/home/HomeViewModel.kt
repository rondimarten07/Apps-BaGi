package com.rondi.bagiapp.ui.home

import androidx.lifecycle.*
import com.rondi.bagiapp.data.remote.ApiResponse
import com.rondi.bagiapp.data.remote.response.ItemsResponse
import com.rondi.bagiapp.data.repository.ItemsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    fun getAllItem(token: String) : LiveData<ApiResponse<ItemsResponse>> {
        val result = MutableLiveData<ApiResponse<ItemsResponse>>()
        viewModelScope.launch {
            itemsRepository.getAllItem(token).collect {
                result.postValue(it)
            }
        }
        return result
    }

}
