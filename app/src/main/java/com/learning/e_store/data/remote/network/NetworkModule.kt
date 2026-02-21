package com.learning.e_store.data.remote.network

import com.learning.e_store.core.session.SessionEventManager
import com.learning.e_store.core.session.SessionManager
import com.learning.e_store.data.remote.api.UserApi
import com.learning.e_store.data.remote.api.CartApi
import com.learning.e_store.data.remote.api.CategoryApi
import com.learning.e_store.data.remote.api.OrderApi
import com.learning.e_store.data.remote.api.ProductApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    private const val BASE_URL = "https://tirkey-eshop.up.railway.app/"
    
    private val json = Json {
        ignoreUnknownKeys = true          // Prevent crash if API adds new fields
        coerceInputValues = true          // Prevent nullability mismatch crash
        prettyPrint = false
        isLenient = true
    }
    
    @Provides
    @Singleton
    fun provideAuthInterceptor(
        sessionManager: SessionManager,
        sessionEventManager: SessionEventManager
    ): AuthInterceptor = AuthInterceptor(sessionManager, sessionEventManager)
    
    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .build()
    
    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    
    
    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): UserApi =
        retrofit.create(UserApi::class.java)
    
    @Provides
    @Singleton
    fun provideProductApi(retrofit: Retrofit): ProductApi = 
        retrofit.create(ProductApi::class.java)
    
    @Provides
    @Singleton
    fun provideCartApi(retrofit: Retrofit): CartApi =
        retrofit.create(CartApi::class.java)

    @Provides
    @Singleton
    fun provideOrderApi(retrofit: Retrofit): OrderApi {
        return retrofit.create(OrderApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideCategoryApi(retrofit: Retrofit): CategoryApi {
        return retrofit.create(CategoryApi::class.java)
    }
    
    
}

//  <----        Split Network Module into multiple module     ---->