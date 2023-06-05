package com.rondi.bagiapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import com.rondi.bagiapp.MainActivity
import com.rondi.bagiapp.databinding.ActivitySplashScreenBinding
import com.rondi.bagiapp.ui.login.LoginFragment.Companion.EXTRA_TOKEN
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
@ExperimentalPagingApi
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private val splashTimeOut: Long = 3000 // 3 detik
    private val viewModel: SplashScreenViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater);
        setContentView(binding.root)

        supportActionBar?.hide()


        binding.root.postDelayed(
            {
                checkUser()
            },
            splashTimeOut
        )
    }

    private fun checkUser(){
        lifecycleScope.launchWhenCreated {
            launch {
                viewModel.getAuthToken().collect { token ->
                    if (token.isNullOrEmpty()) {

                        Intent(
                            this@SplashScreenActivity, OnBoardingActivity::class.java
                        ).also { intent ->
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Intent(
                            this@SplashScreenActivity, MainActivity::class.java
                        ).also { intent ->
                            intent.putExtra(EXTRA_TOKEN, token)
                            startActivity(intent)
                            finish()
                        }
                    }

                }
            }
        }
    }
}