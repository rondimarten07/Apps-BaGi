package com.rondi.bagiapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rondi.bagiapp.data.remote.response.ItemsItem
import com.rondi.bagiapp.databinding.SlidesItemsBinding
import com.rondi.bagiapp.utils.setImageFromUrl

class PhotoItemsAdapter (private var items : List<String>) :
    RecyclerView.Adapter<PhotoItemsAdapter.PhotoItemsViewHolder>()
{
    inner class PhotoItemsViewHolder(private val binding : SlidesItemsBinding) :
        RecyclerView.ViewHolder(binding.root)
    {

        fun bind(photoUrl :String)
        {
            Glide.with(binding.root)
                .load(items[position])
                .centerCrop()
                .into(binding.detailPhotoItems)

        }

    }


    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : PhotoItemsViewHolder
    {
        return PhotoItemsViewHolder(
            SlidesItemsBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        )
    }

    override fun onBindViewHolder(holder : PhotoItemsViewHolder , position : Int)
    {
        holder.bind(items[position])
    }

    override fun getItemCount() : Int
    {
        return items.size
    }
}
