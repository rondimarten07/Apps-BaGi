package com.rondi.bagiapp.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import com.rondi.bagiapp.data.datasource.ItemsDataSource
import com.rondi.bagiapp.data.local.entity.ItemsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton


@ExperimentalPagingApi
@Singleton
class ItemsRepository @Inject constructor(private val itemsDataSource: ItemsDataSource) {

    fun getAllItems(token: String): Flow<PagingData<ItemsEntity>> {
        return itemsDataSource.getAllItems(token).flowOn(Dispatchers.IO)
    }
}