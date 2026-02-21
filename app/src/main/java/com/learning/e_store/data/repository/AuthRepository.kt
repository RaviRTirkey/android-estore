package com.learning.e_store.data.repository

import android.util.Log
import com.learning.e_store.core.session.SessionManager
import com.learning.e_store.data.remote.api.UserApi
import com.learning.e_store.data.remote.api.model.LoginRequest
import com.learning.e_store.data.remote.api.model.RegisterRequest
import com.learning.e_store.data.remote.api.model.UserDetails
import okhttp3.MultipartBody
import retrofit2.HttpException
import retrofit2.http.Multipart
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

sealed class AuthResult {
    object Success : AuthResult()
    object InvalidCredentials : AuthResult()
    object NetworkError : AuthResult()
    object UnknownError : AuthResult()
}

class AuthRepository @Inject constructor(
    private val userApi: UserApi,
    private val sessionManager: SessionManager
) {

    suspend fun registerUser(email: String, name: String, password: String): AuthResult {
        val response = userApi.userRegistration(RegisterRequest(email, name, password))
        return try {
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.token.isNullOrBlank()) {
                    return AuthResult.UnknownError
                }
                sessionManager.saveAuthToken(body.token)
                AuthResult.Success
            } else {
                when (response.code()) {
                    401 -> AuthResult.InvalidCredentials
                    else -> AuthResult.UnknownError
                }
            }
        } catch (e: IOException) {
            AuthResult.NetworkError
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            AuthResult.UnknownError
        }


    }

    suspend fun loginUser(email: String, password: String): AuthResult {
        Log.d("LoginUser", "LoginUser method called")
        val response = userApi.userLogin(LoginRequest(email, password))

        Log.d("AuthRepo", "HTTP ${response.code()}")

        return try {

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.token.isNullOrBlank()) {
                    Log.e("AuthRepo", "Token missing in response")
                    return AuthResult.UnknownError
                }

                sessionManager.saveAuthToken(body.token)
                AuthResult.Success
            } else {
                when (response.code()) {
                    401 -> AuthResult.InvalidCredentials
                    else -> AuthResult.UnknownError
                }
            }

        } catch (e: IOException) {
            AuthResult.NetworkError
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("AuthRepo", "Unexpected error", e)
            AuthResult.UnknownError
        }
    }
    
    fun logout(){
        sessionManager.clearData()
    }
    
    suspend fun uploadProfilePicture(image: MultipartBody.Part): UserDetails{
        val response = userApi.uploadPicture(image)
        Log.d("AuthRepository", "uploadProfilePicture: ${response.body()}")
        
        if (response.isSuccessful){
            Log.d("AuthRepository", "uploadProfilePicture: ${response.body()}")
            return response.body()!!
        }else{
            Log.d("AuthRepository", "uploadProfilePicture: ${response.code()}: ${response.message()}")
            throw Exception("Failed to fetch user details")
        }
        
    }
    
    suspend fun getUserDetails(): UserDetails{
        val response = userApi.getUserDetails()
        if (response.isSuccessful){
            return response.body()!!
        }else{
            throw Exception("Failed to fetch user details")
        }
    }

    suspend fun validateSavedToken(): Boolean {
        sessionManager.fetchAuthToken() ?: return false

        return try {
            userApi.getUserDetails()
            true
        } catch (e: HttpException) {
            if (e.code() == 401){
                sessionManager.clearData()
            }
            false
        } catch (e: Exception) {
            false
        }
    }

}