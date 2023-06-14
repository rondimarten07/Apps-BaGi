package com.rondi.bagiapp.data.repository


import com.rondi.bagiapp.data.local.AuthPreferences
import com.rondi.bagiapp.data.remote.response.LoginResponse
import com.rondi.bagiapp.data.remote.response.RegisterResponse
import com.rondi.bagiapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val preferences: AuthPreferences
) {

    suspend fun userLogin(email: String, password: String): Flow<Result<LoginResponse>> = flow {
        try {
            val response = apiService.userLogin(email, password)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun userRegister(
        nama: String,
        email: String,
        phone: String,
        username: String,
        password: String,
    ): Flow<Result<RegisterResponse>> = flow {
        try {
            val response = apiService.userRegister(nama, email, phone, username, password)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)


    suspend fun saveAuthToken(token: String) {
        preferences.saveAuthToken(token)
    }


    fun getAuthToken(): Flow<String?> = preferences.getAuthToken()

    suspend fun saveAuthUserId(userId: String){
        preferences.saveAuthUserId(userId)
    }

    fun getUserId(): Flow<String?> = preferences.getAuthUserId()
}
