package com.rondi.bagiapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rondi.bagiapp.data.local.entity.ItemsEntity
import com.rondi.bagiapp.data.repository.ItemsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    fun getAllItems(token: String) : LiveData<PagingData<ItemsEntity>> =
        itemsRepository.getAllItems(token).cachedIn(viewModelScope).asLiveData()
}
