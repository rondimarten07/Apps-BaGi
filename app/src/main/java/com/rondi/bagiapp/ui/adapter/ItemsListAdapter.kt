package com.rondi.bagiapp.ui.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rondi.bagiapp.data.local.entity.ItemsEntity
import com.rondi.bagiapp.databinding.LayoutItemsBinding
import com.rondi.bagiapp.ui.detail.DetailItemsActivity
import com.rondi.bagiapp.utils.setImageFromUrl
import com.rondi.bagiapp.utils.timeStamptoString


class ItemsListAdapter: PagingDataAdapter<ItemsEntity, ItemsListAdapter.ViewHolder>(
    DIFF_CALLBACK) {

    class ViewHolder(private val binding: LayoutItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, item : ItemsEntity) {
            binding.apply {

                Glide.with(itemView).load(item.photoUrl).into(ivItemPhoto)
                tvItemName.text = item.name
                tvItemNohp.text = item.nohp
                tvTitle.text = item.title
                tvItemDate.text = item.createAt.timeStamptoString()
                imgItems.setImageFromUrl(item.photoItems.first())
                tvItemDescription.text = item.description


                root.setOnClickListener {

//                    val optionsCompat: ActivityOptionsCompat =
//                        ActivityOptionsCompat.makeSceneTransitionAnimation(
//                            root.context as Activity,
//                            Pair(tvItemPhoto, "img_detail"),
//                            Pair(tvItemName, "title"),
//                            Pair(tvItemDate, "date"),
//                            Pair(tvItemDescription, "description")
//                        )

                    Intent(it.context, DetailItemsActivity::class.java).also { intent ->
                        intent.putExtra("EXTRA_DETAIL", item)
                        it.context.startActivity(intent)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LayoutItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(holder.itemView.context, item)
        }

    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ItemsEntity>() {
            override fun areItemsTheSame(oldItem: ItemsEntity, newItem: ItemsEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ItemsEntity, newItem: ItemsEntity): Boolean {
                return oldItem == newItem
            }
        }
    }

}