package com.rondi.bagiapp.ui

import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.rondi.bagiapp.OnBoardingModel
import com.rondi.bagiapp.databinding.ActivityOnBoardingBinding
import com.rondi.bagiapp.ui.adapter.OnBoardingItemAdapter
import com.rondi.bagiapp.R.string
import com.rondi.bagiapp.R.drawable

class OnBoardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnBoardingBinding
    private lateinit var dots: LinearLayout
    private var prevStarted = "yes"
    private lateinit var onBoardingItemAdapter: OnBoardingItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        setOnBoardingItems()
        setDotsOnBoarding()
        setCurrentDotsOnBoarding(0)
    }

    override fun onResume()
    {
        super.onResume()
        val sharedPreferences =
            getSharedPreferences(getString(string.app_name) , Context.MODE_PRIVATE)
        if (!sharedPreferences.getBoolean(prevStarted , false))
        {
            val editor = sharedPreferences.edit()
            editor.putBoolean(prevStarted , true)
            editor.apply()
        } else
        {
            val intent = Intent(this@OnBoardingActivity , AuthActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setOnBoardingItems() {
        onBoardingItemAdapter = OnBoardingItemAdapter(
            listOf(
                OnBoardingModel(
                    drawable.img_onboarding_1,
                    getString(string.title_onboard_1),
                    getString(string.desc_onboard_1)
                ),
                OnBoardingModel(
                    drawable.img_onboarding_2,
                    getString(string.title_onboard_2),
                    getString(string.desc_onboard_2)
                ),
                OnBoardingModel(
                    drawable.img_onboarding_3,
                    getString(string.title_onboard_3),
                    getString(string.desc_onboard_3)
                )
            )
        )
        val viewPager = binding.vpOnBoard
        viewPager.adapter = onBoardingItemAdapter
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentDotsOnBoarding(position)
            }

        })
        (viewPager.getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        binding.ButtonNext.setOnClickListener {
            if (viewPager.currentItem + 1 < onBoardingItemAdapter.itemCount) {
                viewPager.currentItem += 1
            } else {
                val intent = Intent(this@OnBoardingActivity, AuthActivity::class.java)
                startActivity(intent)
                finish()
            }

        }


    }

    private fun setDotsOnBoarding()
    {
        dots = binding.indicator

        val indicator = arrayOfNulls<ImageView>(onBoardingItemAdapter.itemCount)
        val layoutParameter : LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(WRAP_CONTENT , WRAP_CONTENT)
        layoutParameter.setMargins(16 , 0 , 0 , 0)

        for (i in indicator.indices)
        {
            indicator[i] = ImageView(applicationContext)
            indicator[i]?.let {
                it.setImageDrawable(
                    ContextCompat.getDrawable(applicationContext , drawable.indicator_inactive)
                )
                it.layoutParams = layoutParameter
                dots.addView(it)
            }
        }
    }

    fun setCurrentDotsOnBoarding(position : Int)
    {
        val childCount = dots.childCount
        for (i in 0 until childCount)
        {
            val imageView = dots.getChildAt(i) as ImageView
            if (i == position)
            {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(applicationContext , drawable.indicator_active)
                )
            } else
            {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(applicationContext , drawable.indicator_inactive)
                )
            }

        }

        val lastPosition = childCount - 1
        val buttonText = if (position == lastPosition) "Get Started" else "Next"
        binding.ButtonNext.text = buttonText
    }

}
