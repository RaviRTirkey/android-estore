package com.learning.e_store.data.remote.api

import com.learning.e_store.data.remote.api.model.AuthResponse
import com.learning.e_store.data.remote.api.model.ImageResponse
import com.learning.e_store.data.remote.api.model.LoginRequest
import com.learning.e_store.data.remote.api.model.RegisterRequest
import com.learning.e_store.data.remote.api.model.UserDetails
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UserApi {
    @POST("api/v1/user/auth/register")
    suspend fun userRegistration(
        @Body registerRequest: RegisterRequest
    ): Response<AuthResponse>

    @POST("api/v1/user/auth/login")
    suspend fun userLogin(
        @Body loginRequest: LoginRequest
    ): Response<AuthResponse>
    
    @Multipart
    @POST("api/v1/user/profile/upload")
    suspend fun uploadPicture(
        @Part image: MultipartBody.Part
    ): Response<UserDetails>
    
    @GET("api/v1/user/profile/user")
    suspend fun getUserDetails(): Response<UserDetails>
}