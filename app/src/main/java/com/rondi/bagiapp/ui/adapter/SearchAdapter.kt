package com.rondi.bagiapp.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rondi.bagiapp.data.remote.response.ItemsItem
import com.rondi.bagiapp.data.remote.response.SearchItem
import com.rondi.bagiapp.databinding.LayoutItemsBinding
import com.rondi.bagiapp.ui.detail.DetailItemsActivity
import com.rondi.bagiapp.utils.setImageFromUrl
import com.rondi.bagiapp.utils.timeStamptoString


class SearchAdapter(private val searchList: List<SearchItem>) :
    RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchAdapter.SearchViewHolder {
        val binding = LayoutItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchAdapter.SearchViewHolder, position: Int) {
        searchList[position].let { search ->
            search.let { holder.bind(it) }
        }
    }

    override fun getItemCount(): Int = searchList.size

    inner class SearchViewHolder(private val binding: LayoutItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SearchItem) {
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