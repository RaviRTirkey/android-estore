package com.learning.e_store.data.remote.network

import com.learning.e_store.core.session.SessionEventManager
import com.learning.e_store.core.session.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val sessionManager: SessionManager,
    private val sessionEventManager: SessionEventManager
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        
        val token = sessionManager.fetchAuthToken()

        val request = chain.request().newBuilder().apply {
            token?.let {
                addHeader("Authorization", "Bearer $it")
            } 
        }.build()
        
        val response = chain.proceed(request)
        
        if (response.code == 401){
            if (token != null){
                sessionManager.clearData()

                // Launch coroutine manually
                CoroutineScope(Dispatchers.IO).launch {
                    sessionEventManager.notifySessionExpired()
                }
            }
        }
        
        return response
        
    }
}