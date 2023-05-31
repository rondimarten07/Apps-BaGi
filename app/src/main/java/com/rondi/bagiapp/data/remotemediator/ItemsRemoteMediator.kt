package com.rondi.bagiapp.data.remotemediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.rondi.bagiapp.data.local.db.ItemsDatabase
import com.rondi.bagiapp.data.local.entity.ItemsEntity
import com.rondi.bagiapp.data.local.entity.RemoteKeysEntity
import com.rondi.bagiapp.data.mapping.itemsToItemsEntity
import com.rondi.bagiapp.data.remote.retrofit.ApiService

@OptIn(ExperimentalPagingApi::class)
class ItemsRemoteMediator(
    private val database: ItemsDatabase,
    private val service: ApiService,
    private val token: String
) : RemoteMediator<Int, ItemsEntity>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }


    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ItemsEntity>
    ): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                nextKey
            }
        }

        try {
            val responseData = service.getAllItems(token, page, state.config.pageSize)
            val itemsList = responseData.listItems?.map {
                if (it != null) {
                    itemsToItemsEntity(it)
                }
            }

            val endOfPagination = responseData.listItems?.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.getRemoteKeysDao().deleteRemoteKeys()
                    database.ItemsDao().deleteAllItems()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPagination == true) null else page + 1
                val keys = responseData.listItems?.map {
                    RemoteKeysEntity(id = it?.id ?: "", prevKey = prevKey, nextKey = nextKey)
                }

                if (keys != null) {
                    database.getRemoteKeysDao().insertAll(keys)
                }

                responseData.listItems?.forEach {
                    val itemsEntity = it?.let { it1 -> itemsToItemsEntity(it1) }

                    if (itemsEntity != null) {
                        database.ItemsDao().insertItems(itemsEntity)
                    }
                }
            }
            return MediatorResult.Success(endOfPagination == true)
        } catch (ex: Exception) {
            return MediatorResult.Error(ex)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ItemsEntity>): RemoteKeysEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            data.id?.let { database.getRemoteKeysDao().getRemoteKeysId(it) }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ItemsEntity>): RemoteKeysEntity? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            data.id?.let { database.getRemoteKeysDao().getRemoteKeysId(it) }
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, ItemsEntity>): RemoteKeysEntity? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.getRemoteKeysDao().getRemoteKeysId(id)
            }
        }
    }
}