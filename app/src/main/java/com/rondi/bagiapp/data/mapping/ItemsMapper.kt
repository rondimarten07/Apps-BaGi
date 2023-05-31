package com.rondi.bagiapp.data.mapping

import com.rondi.bagiapp.data.local.entity.ItemsEntity
import com.rondi.bagiapp.data.remote.response.ListItemsItem

fun itemsToItemsEntity(items: ListItemsItem): ItemsEntity {
    return ItemsEntity(
        id = items.id,
        name = items.name,
        description = items.description,
        photoUrl = items.photoUrl,
        createdAt = items.createdAt,
        nohp = items.nohp,
        loc = items.loc,
        kategori = items.kategori
    )
}