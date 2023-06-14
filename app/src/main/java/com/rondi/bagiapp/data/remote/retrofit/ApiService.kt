package com.rondi.bagiapp.data.remote.retrofit

import com.rondi.bagiapp.data.remote.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("/login")
    suspend fun userLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @FormUrlEncoded
    @POST("/register")
    suspend fun userRegister(
        @Field("nama") nama: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("username") username: String,
        @Field("password") password: String
    ): RegisterResponse

    @GET("/items")
    suspend fun getAllItem(
        @Header("Authorization") token: String,
    ): ItemsResponse

    @GET("/items/search")
    suspend fun searchItems(
        @Header("Authorization") token: String,
        @Query("keyword") keyword: String? = null,
    ): SearchResponse

    @GET("/items/user")
    suspend fun getMyItem(
        @Header("Authorization") token: String,
        @Query("user_id") userId : String
    ): MyItemResponse

    @GET("/profile")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): ProfileResponse

    @Multipart
    @PUT("/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("nama") nama: RequestBody,
        @Part("username") username: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("loc") loc: RequestBody
    ): UpdateProfileResponse

    @Multipart
    @POST("/upload_item")
    suspend fun uploadItems(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("category") category: RequestBody,
    ): UploadItemsResponse

}