package com.rondi.bagiapp.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rondi.bagiapp.data.remote.ApiResponse
import com.rondi.bagiapp.data.remote.response.ProfileResponse
import com.rondi.bagiapp.data.remote.response.UpdateProfileResponse
import com.rondi.bagiapp.data.repository.AuthRepository
import com.rondi.bagiapp.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    fun getProfile(token: String): LiveData<ApiResponse<ProfileResponse>> {
        val result = MutableLiveData<ApiResponse<ProfileResponse>>()
        viewModelScope.launch {
            profileRepository.getProfile(token)
                .collect { response ->
                    result.postValue(response)
                }
        }
        return result
    }

    fun getAuthToken(): Flow<String?> = authRepository.getAuthToken()

    fun updateProfile(
        token: String,
        file: MultipartBody.Part,
        nama: RequestBody,
        username: RequestBody,
        phone: RequestBody,
        loc: RequestBody
    ): MutableLiveData<ApiResponse<UpdateProfileResponse>> {
        val result = MutableLiveData<ApiResponse<UpdateProfileResponse>>()
        viewModelScope.launch {
            profileRepository.updateProfile(token, file, nama, username, phone, loc).collect {
                result.postValue(it)
            }
        }
        return result
    }

    fun saveAuthToken(token: String) {
        viewModelScope.launch {
            authRepository.saveAuthToken(token)
        }
    }

}