package com.rondi.bagiapp.ui.myitem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.rondi.bagiapp.R
import com.rondi.bagiapp.data.remote.ApiResponse
import com.rondi.bagiapp.databinding.FragmentMyitemBinding
import com.rondi.bagiapp.ui.adapter.MyItemsAdapter
import com.rondi.bagiapp.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@ExperimentalPagingApi
@AndroidEntryPoint
class MyItemFragment : Fragment() {
    private var _binding: FragmentMyitemBinding? = null
    private val binding get() = _binding!!
    private var token: String = ""
    private var userId: String = ""
    private val viewModel : MyItemViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyitemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.rvMyItems.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launchWhenResumed {
            launch {
                viewModel.getAuthToken().collect { authToken ->
                    viewModel.getAuthUserId().collect{idUser ->
                        if (!authToken.isNullOrEmpty() && !idUser.isNullOrEmpty()){
                            token = authToken
                            userId = idUser
                        }
                        getMyItems("Bearer $token", userId)
                    }

                }
            }

        }
    }

    private fun getMyItems(token: String, userId: String) {
        viewModel.getMyItems(token, userId).observe(requireActivity()) { response ->
            when (response) {
                is ApiResponse.Loading -> isLoading(true)
                is ApiResponse.Success -> {
                    isLoading(false)
                    val adapter = response.data.item?.let { MyItemsAdapter(it) }

                    if (adapter?.itemCount == 0){
                        binding.iluastarasiNoData.alpha = 1f
                    }else{
                        binding.rvMyItems.adapter = adapter
                    }

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
                binding.progressBar.visibility = View.VISIBLE
            }
        } else {
            binding.apply {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}