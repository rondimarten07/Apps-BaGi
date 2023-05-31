package com.rondi.bagiapp.data.remote.retrofit

import com.rondi.bagiapp.data.remote.response.ItemsResponse
import com.rondi.bagiapp.data.remote.response.LoginResponse
import com.rondi.bagiapp.data.remote.response.RegisterResponse
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("login")
    suspend fun userLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @FormUrlEncoded
    @POST("register")
    suspend fun userRegister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("nohp") nohp: Int,
        @Field("username") username: String,
        @Field("password") password: String
    ): RegisterResponse

    @GET("items")
    suspend fun getAllItems(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
    ): ItemsResponse
}