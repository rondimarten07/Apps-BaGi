package com.rondi.bagiapp.ui.register

import androidx.lifecycle.ViewModel
import com.rondi.bagiapp.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    suspend fun userRegister(name: String, email: String, phone: String, username: String, password: String) =
        authRepository.userRegister(name, email, phone, username, password)
}