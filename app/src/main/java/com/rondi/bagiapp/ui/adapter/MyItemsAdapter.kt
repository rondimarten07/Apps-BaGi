package com.rondi.bagiapp.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rondi.bagiapp.data.remote.response.MyItem
import com.rondi.bagiapp.databinding.LayoutMyitemBinding
import com.rondi.bagiapp.ui.detail.DetailItemsActivity
import com.rondi.bagiapp.utils.setImageFromUrl
import com.rondi.bagiapp.utils.timeStamptoString


class MyItemsAdapter(private val myItemList: List<MyItem?>): RecyclerView.Adapter<MyItemsAdapter.MyItemsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyItemsAdapter.MyItemsViewHolder {
        val binding = LayoutMyitemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyItemsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyItemsAdapter.MyItemsViewHolder, position: Int) {
        myItemList[position].let { myItem ->
            myItem?.let { holder.bind(it) }
        }
    }

    override fun getItemCount(): Int = myItemList.size

    inner class MyItemsViewHolder(private val binding: LayoutMyitemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(myItem : MyItem?) {
            with(binding) {
                myItem?.photoItems?.let { ivMyitem.setImageFromUrl(it) }
                title.text = myItem?.title
                description.text = myItem?.description
                myItem?.photoUrl?.let { ivItemPhoto.setImageFromUrl(it) }
                tvItemName.text = myItem?.name
                tvItemNohp.text = myItem?.name
                tvItemDate.text = myItem?.createAt?.timeStamptoString()
            }
            itemView.setOnClickListener {
                val intent = Intent(it.context, DetailItemsActivity::class.java)
                intent.putExtra("EXTRA_DETAIL", myItem)
                it.context.startActivity(intent)
            }
        }
    }

}