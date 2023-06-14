package com.rondi.bagiapp.ui.detail

import androidx.lifecycle.ViewModel
import com.rondi.bagiapp.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {


    fun getAuthToken(): Flow<String?> = authRepository.getAuthToken()

    fun getAuthUserId(): Flow<String?> = authRepository.getUserId()
}