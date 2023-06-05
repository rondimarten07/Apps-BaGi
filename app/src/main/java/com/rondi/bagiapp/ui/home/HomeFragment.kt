package com.rondi.bagiapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.rondi.bagiapp.R
import com.rondi.bagiapp.data.local.entity.ItemsEntity
import com.rondi.bagiapp.data.remote.response.LoginResponse
import com.rondi.bagiapp.databinding.FragmentHomeBinding
import com.rondi.bagiapp.ui.adapter.ItemsListAdapter
import com.rondi.bagiapp.ui.adapter.LoadingStateAdapter
import com.rondi.bagiapp.utils.isTrue
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import com.rondi.bagiapp.utils.showToast

@Suppress("DEPRECATION")
@ExperimentalPagingApi
@AndroidEntryPoint

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var listAdapter: ItemsListAdapter


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

        getDataItems()

        getAllItems("Bearer ${token}")

    }

        private fun getDataItems() {
            listAdapter = ItemsListAdapter()

            binding.rvItems.layoutManager?.onRestoreInstanceState(binding.rvItems.layoutManager?.onSaveInstanceState())
            binding.rvItems.adapter = listAdapter

            lifecycleScope.launch {
                listAdapter.loadStateFlow.distinctUntilChanged { old, new ->
                    old.mediator?.prepend?.endOfPaginationReached.isTrue() == new.mediator?.prepend?.endOfPaginationReached.isTrue()
                }
                    .filter { it.refresh is LoadState.NotLoading && it.prepend.endOfPaginationReached && !it.append.endOfPaginationReached }
                    .collect {
                        binding.rvItems.smoothScrollToPosition(0)
                    }
            }

            listAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    listAdapter.retry()
                }
            )

            listAdapter.addLoadStateListener { loadState ->
                when (loadState.refresh) {
                    is LoadState.Loading -> {
                        isError(false)
                        isLoading(true)
                    }
                    is LoadState.NotLoading -> {
                        isError(false)
                        isLoading(false)
                    }
                    is LoadState.Error -> {
                        isError(true)
                        isLoading(false)
                    }
                    else -> {
                        showToast(getString(R.string.message_unknown_state))
                    }
                }
            }
        }

        private fun getAllItems(token: String) {
            viewModel.getAllItems(token).observe(requireActivity()) { items ->
                initRecyclerViewUpdate(items)
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

        private fun initRecyclerViewUpdate(storiesData: PagingData<ItemsEntity>) {
            val recyclerViewState = binding.rvItems.layoutManager?.onSaveInstanceState()

            listAdapter.submitData(lifecycle, storiesData)
            binding.rvItems.layoutManager?.onRestoreInstanceState(recyclerViewState)

        }


        private fun isError(error: Boolean) {
            if (error) {
                binding.ivNotFoundError.alpha = 1f
                binding.tvNotFoundError.alpha = 1f
            } else {
                binding.rvItems.visibility = View.VISIBLE
            }
        }

        companion object {
            const val EXTRA_TOKEN = "extra_token"
        }



    }