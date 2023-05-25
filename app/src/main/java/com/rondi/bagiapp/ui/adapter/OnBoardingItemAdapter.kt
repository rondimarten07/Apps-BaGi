package com.rondi.bagiapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rondi.bagiapp.OnBoardingModel
import com.rondi.bagiapp.databinding.SlidesOnboardingItemBinding


class OnBoardingItemAdapter(private var onBoardingModel : List<OnBoardingModel>) :
    RecyclerView.Adapter<OnBoardingItemAdapter.OnBoardingItemViewHolder>()
{


    inner class OnBoardingItemViewHolder(private val binding : SlidesOnboardingItemBinding) :
        RecyclerView.ViewHolder(binding.root)
    {


        fun bind(onBoardingModel : OnBoardingModel)
        {
            binding.imgSlides.setImageResource(onBoardingModel.imgSlides)
            binding.Title.text = onBoardingModel.title
            binding.DescOnBoard.text = onBoardingModel.desc
        }

    }


    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : OnBoardingItemViewHolder
    {
        return OnBoardingItemViewHolder(
            SlidesOnboardingItemBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        )
    }

    override fun onBindViewHolder(holder : OnBoardingItemViewHolder , position : Int)
    {
        holder.bind(onBoardingModel[position])
    }

    override fun getItemCount() : Int
    {
        return onBoardingModel.size
    }
}