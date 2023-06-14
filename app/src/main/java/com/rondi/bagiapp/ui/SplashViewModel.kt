package com.rondi.bagiapp.ui

import androidx.lifecycle.ViewModel
import com.rondi.bagiapp.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(private val authRepository: AuthRepository) :
    ViewModel() {

    fun getAuthToken(): Flow<String?> = authRepository.getAuthToken()

}