package com.rondi.bagiapp.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rondi.bagiapp.data.remote.response.ItemsItem
import com.rondi.bagiapp.databinding.LayoutItemsBinding
import com.rondi.bagiapp.ui.detail.DetailItemsActivity
import com.rondi.bagiapp.utils.setImageFromUrl
import com.rondi.bagiapp.utils.timeStamptoString


class ItemListAdapter(private val ItemList: List<ItemsItem>) :
    RecyclerView.Adapter<ItemListAdapter.ItemListViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemListAdapter.ItemListViewHolder {
        val binding = LayoutItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemListAdapter.ItemListViewHolder, position: Int) {
        ItemList[position].let { itemList ->
            itemList.let { holder.bind(it) }
        }
    }

    override fun getItemCount(): Int = ItemList.size

    inner class ItemListViewHolder(private val binding: LayoutItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ItemsItem) {
            with(binding) {
                Glide.with(itemView).load(item.photoUrl).into(ivItemPhoto)
                tvItemName.text = item.name
                tvItemNohp.text = item.nohp
                tvTitle.text = item.title
                tvItemDate.text = item.createAt.timeStamptoString()
                imgItems.setImageFromUrl(item.photoItems)
                tvItemDescription.text = item.description
            }
            itemView.setOnClickListener {
                val intent = Intent(it.context, DetailItemsActivity::class.java)
                intent.putExtra("EXTRA_DETAIL", item)
                it.context.startActivity(intent)
            }
        }
    }

}