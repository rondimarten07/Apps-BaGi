package com.rondi.bagiapp.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.rondi.bagiapp.R
import com.rondi.bagiapp.data.local.entity.ItemsEntity
import com.rondi.bagiapp.databinding.ActivityDetailItemsBinding
import com.rondi.bagiapp.ui.adapter.PhotoItemsAdapter
import com.rondi.bagiapp.utils.setImageFromUrl

@Suppress("DEPRECATION")
class DetailItemsActivity : AppCompatActivity() {
    private var _binding: ActivityDetailItemsBinding? = null
    private val binding get() = _binding!!
    private lateinit var photoItemsAdapter: PhotoItemsAdapter
    private lateinit var dots: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailItemsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val items = intent.getParcelableExtra<ItemsEntity>("EXTRA_DETAIL")
        val photoItemsList: List<String>? = items?.photoItems

        photoItemsAdapter = PhotoItemsAdapter(photoItemsList ?: emptyList())
        binding.vpItems.adapter = photoItemsAdapter
        binding.vpItems.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentDotsOnBoarding(position)
            }

        })


        binding.apply {
            if (items != null) {
                detailProfile.setImageFromUrl(items.photoUrl)
                detailName.text = items.name
                detailNohp.text = items.nohp
                detailTitle.text = items.title
                detailDate.text = items.createAt
                detailLoc.text = items.loc
                detailKategori.text = items.kategori
                detailDesc.text = items.description
            }
        }

        setDotsOnBoarding()
        setCurrentDotsOnBoarding(0)

        binding.btnBack.setOnClickListener{
            onBackPressed()
        }
    }


    private fun setDotsOnBoarding() {
        dots = binding.indicator

        val indicator = arrayOfNulls<ImageView>(photoItemsAdapter.itemCount)
        val layoutParameter: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        layoutParameter.setMargins(16, 0, 0, 0)

        for (i in indicator.indices) {
            indicator[i] = ImageView(applicationContext)
            indicator[i]?.let {
                it.setImageDrawable(
                    ContextCompat.getDrawable(applicationContext, R.drawable.indicator_inactive)
                )
                it.layoutParams = layoutParameter
                dots.addView(it)
            }
        }
    }

    fun setCurrentDotsOnBoarding(position: Int) {
        val childCount = dots.childCount
        for (i in 0 until childCount) {
            val imageView = dots.getChildAt(i) as ImageView
            if (i == position) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(applicationContext, R.drawable.indicator_active)
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(applicationContext, R.drawable.indicator_inactive)
                )
            }

        }
    }

}