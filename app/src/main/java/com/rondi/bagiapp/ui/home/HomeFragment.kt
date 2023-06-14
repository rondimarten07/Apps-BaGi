package com.rondi.bagiapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.rondi.bagiapp.R
import com.rondi.bagiapp.data.remote.ApiResponse
import com.rondi.bagiapp.databinding.FragmentHomeBinding
import com.rondi.bagiapp.ui.SearchItemActivity
import com.rondi.bagiapp.ui.adapter.ItemListAdapter
import dagger.hilt.android.AndroidEntryPoint
import com.rondi.bagiapp.utils.showToast

@Suppress("DEPRECATION")
@AndroidEntryPoint

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var token: String = ""
    private val viewModel: HomeViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        token = requireActivity().intent.getStringExtra(EXTRA_TOKEN)!!

        binding.rvItems.layoutManager = LinearLayoutManager(requireContext())

        getAllItems("Bearer $token")

        binding.search.setOnClickListener {
            val intentToSearch = Intent(requireActivity(), SearchItemActivity::class.java)
            startActivity(intentToSearch)
        }


    }

    private fun getAllItems(token: String) {
        viewModel.getAllItem(token).observe(requireActivity()) { response ->
            when (response) {
                is ApiResponse.Loading -> isLoading(true)
                is ApiResponse.Success -> {
                    isLoading(false)
                    isError(false)
                    val adapter = ItemListAdapter(response.data.items)
                    binding.rvItems.adapter = adapter
                }
                is ApiResponse.Error -> {
                    isLoading(false)
                    showToast(getString(R.string.message_error_items) + response.error)
                    isError(true)

                }
                else -> {
                    showToast(getString(R.string.message_unknown_state))
                    isError(true)
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


    private fun isError(error: Boolean) {
        if (error) {
            binding.ivNotFoundError.alpha = 1f
            binding.tvNotFoundError.alpha = 1f
            binding.rvItems.visibility = View.GONE
        } else {
            binding.rvItems.visibility = View.VISIBLE
        }
    }

    companion object {
        const val EXTRA_TOKEN = "extra_token"
    }

}