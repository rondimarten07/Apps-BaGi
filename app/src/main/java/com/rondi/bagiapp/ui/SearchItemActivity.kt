package com.rondi.bagiapp.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rondi.bagiapp.R
import com.rondi.bagiapp.data.remote.ApiResponse
import com.rondi.bagiapp.databinding.ActivitySearchItemBinding
import com.rondi.bagiapp.ui.adapter.SearchAdapter
import com.rondi.bagiapp.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
@AndroidEntryPoint
class SearchItemActivity : AppCompatActivity() {
    private var _binding: ActivitySearchItemBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModels()
    private var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySearchItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.search.requestFocus()

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.search, InputMethodManager.SHOW_IMPLICIT)

        binding.rvItems.layoutManager = LinearLayoutManager(this)


        lifecycleScope.launchWhenCreated {
            launch {
                viewModel.getAuthToken().collect { authToken ->
                    if (!authToken.isNullOrEmpty()) token = authToken
                    token = "Bearer $token"

                    binding.btnSearch.setOnClickListener {
                        val keyword = binding.search.text.toString()
                        searchItems(token, keyword)
                    }

                }
            }
        }

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

    }


    private fun searchItems(token: String, keyword: String) {
        viewModel.searchItem(token, keyword).observe(this) { response ->
            when (response) {
                is ApiResponse.Loading -> isLoading(true)
                is ApiResponse.Success -> {
                    isLoading(false)
                    val adapter = SearchAdapter(response.data.items)
                    binding.rvItems.adapter = adapter
                }
                is ApiResponse.Error -> {
                    isLoading(false)
                    showToast(getString(R.string.message_error_items) + response.error)

                }
                else -> {
                    showToast(getString(R.string.message_unknown_state))
                }
            }
        }
    }

    private fun isLoading(loading: Boolean) {
        if (loading) {
            binding.apply {
                binding.rvItems.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            }
        } else {
            binding.apply {
                binding.rvItems.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }
        }
    }
}