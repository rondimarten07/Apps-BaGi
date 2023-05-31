package com.rondi.bagiapp.data.datasource

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.rondi.bagiapp.data.local.db.ItemsDatabase
import com.rondi.bagiapp.data.local.entity.ItemsEntity
import com.rondi.bagiapp.data.remote.retrofit.ApiService
import com.rondi.bagiapp.data.remotemediator.ItemsRemoteMediator
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalPagingApi::class)
@Singleton
class ItemsDataSource @Inject constructor(
    private val itemsDatabase: ItemsDatabase,
    private val apiService: ApiService
) {
    private companion object {
        const val DEFAULT_PAGE_SIZE = 10
    }


    fun getAllItems(token: String): Flow<PagingData<ItemsEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = DEFAULT_PAGE_SIZE
            ),
            remoteMediator = ItemsRemoteMediator(itemsDatabase, apiService, token),
            pagingSourceFactory = { itemsDatabase.ItemsDao().getAllItems() }
        ).flow
    }


}