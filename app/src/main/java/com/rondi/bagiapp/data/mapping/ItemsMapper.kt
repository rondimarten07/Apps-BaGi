package com.rondi.bagiapp.data.mapping

import com.rondi.bagiapp.data.local.entity.ItemsEntity
import com.rondi.bagiapp.data.remote.response.ItemsItem

fun itemsToItemsEntity(items: ItemsItem): ItemsEntity {
    return ItemsEntity(
        id = items.id,
        name = items.name,
        title = items.title,
        description = items.description,
        photoUrl = items.photoUrl,
        createAt = items.createAt,
        photoItems = items.photoItems,
        nohp = items.nohp,
        loc = items.loc,
        kategori = items.kategori
    )
}