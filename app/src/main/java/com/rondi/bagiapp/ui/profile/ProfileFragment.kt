package com.rondi.bagiapp.ui.profile


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.rondi.bagiapp.R
import com.rondi.bagiapp.data.remote.ApiResponse
import com.rondi.bagiapp.data.remote.response.Profile
import com.rondi.bagiapp.data.remote.response.ProfileResponse
import com.rondi.bagiapp.databinding.FragmentLoginBinding
import com.rondi.bagiapp.databinding.FragmentProfileBinding
import com.rondi.bagiapp.ui.AuthActivity
import com.rondi.bagiapp.ui.login.LoginViewModel
import com.rondi.bagiapp.utils.setImageFromUrl
import com.rondi.bagiapp.utils.showConfirmationDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@GlideModule
@AndroidEntryPoint
@ExperimentalPagingApi
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()
    private var token: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenCreated {
            launch {
                viewModel.getAuthToken().collect { authToken ->
                    if (!authToken.isNullOrEmpty()) token = authToken
                    showProfile("Bearer $token")
                }
            }

        }



        binding.btnLogout.setOnClickListener {
            showConfirmationDialog(getString(R.string.title_message), getString(R.string.message_logout_confirm), onYesClicked = {

                viewModel.saveAuthToken("")
                Intent(requireContext(), AuthActivity::class.java).also { intent ->
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)

                    activity?.finish()
                }
            })

        }

    }


    fun showProfile(token: String) {
        viewModel.getProfile(token).observe(viewLifecycleOwner) { response ->
            when (response) {
                is ApiResponse.Loading -> setLoadingState(true)
                is ApiResponse.Success -> {
                    setLoadingState(false)
                    val profileResponse = response.data

                    val toUpdate = response.data.profile

                    Glide.with(requireContext()).load(profileResponse.profile?.avatar).into(binding.photoProfile)
                    binding.name.text = profileResponse.profile?.nama
                    binding.username.setText(profileResponse.profile?.username)
                    binding.email.setText(profileResponse.profile?.email)
                    binding.phone.setText(profileResponse.profile?.phone)
                    binding.alamat.setText(profileResponse.profile?.loc)

                    binding.btnEdit.setOnClickListener {
                        val intent = Intent(requireContext(), UpdateProfile::class.java)

                        intent.putExtra("profileResponse", toUpdate)
                        startActivity(intent)
                    }
                }

                is ApiResponse.Error -> {
                    setLoadingState(false)
                    val errorMessage = response.error
                    // Handle error state or show error message
                }
            }
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        binding.apply {

            if (isLoading) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        }
    }
}
