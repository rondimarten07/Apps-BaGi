package com.rondi.bagiapp.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.rondi.bagiapp.MainActivity
import com.rondi.bagiapp.R
import com.rondi.bagiapp.R.string
import com.rondi.bagiapp.databinding.FragmentLoginBinding
import com.rondi.bagiapp.utils.showOKDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private var loginJob: Job = Job()
    private val loginviewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setActions()

    }


    private fun setActions() {
        binding.apply {
            tvToRegister.setOnClickListener(
                Navigation.createNavigateOnClickListener(R.id.action_loginFragment_to_registerFragment)
            )

            btnLogin.setOnClickListener {
                handleLogin()
            }
        }
    }

    private fun handleLogin() {
        val email = binding.edLoginEmail.text.toString().trim()
        val password = binding.edLoginPassword.text.toString()
        setLoadingState(true)


        lifecycleScope.launchWhenResumed {
            if (loginJob.isActive) loginJob.cancel()

            loginJob = launch {
                loginviewModel.userLogin(email, password).collect { result ->
                    result.onSuccess { credentials ->

                        credentials.loginResult?.token?.let { token ->
                            val userId = credentials.loginResult.userId
                            loginviewModel.saveAuthToken(token)
                            userId?.let { loginviewModel.saveAuthUserId(it) }
                            Intent(requireContext(), MainActivity::class.java).also { intent ->
                                intent.putExtra(EXTRA_TOKEN, token)
                                intent.putExtra(EXTRA_USER_ID, userId)
                                startActivity(intent)
                                requireActivity().finish()
                            }
                        }

                        Toast.makeText(
                            requireContext(),
                            getString(string.login_success_message),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    result.onFailure {
                        showOKDialog(getString(string.title_message), getString(string.message_failed_login))

                        setLoadingState(false)
                    }
                }
            }
        }

    }

    private fun setLoadingState(isLoading: Boolean) {
        binding.apply {
            edLoginEmail.isEnabled = !isLoading
            edLoginPassword.isEnabled = !isLoading
            btnLogin.isEnabled = !isLoading

            if (isLoading) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val EXTRA_TOKEN = "extra_token"
        const val EXTRA_USER_ID = "user_id"
    }
}