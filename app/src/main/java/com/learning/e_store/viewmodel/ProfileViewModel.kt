package com.learning.e_store.viewmodel

import android.R.attr.bitmap
import android.R.id.input
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.decode.DecodeUtils.calculateInSampleSize
import com.learning.e_store.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject


data class UserProfile(
    val name: String = "asfd",
    val email: String = "dfasf",
    val profileImage: String = "",
    val isLoading: Boolean = false
)

@HiltViewModel
class ProfileViewModel@Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val authRepository: AuthRepository
): ViewModel() {
    private val _userState = MutableStateFlow(UserProfile())
    val userState = _userState.asStateFlow()

    fun uploadImage(uri: Uri){
        _userState.update { it.copy(isLoading = true) }
        
        viewModelScope.launch(Dispatchers.IO) {
            val maxSize = 1024*1024
            val file = File(context.cacheDir, "profile_pic.jpg")
            
            context.contentResolver.openInputStream(uri)?.use { input ->
                val options = BitmapFactory.Options().apply { 
                    inJustDecodeBounds = true
                }
                
                BitmapFactory.decodeStream(input, null, options)
                
                options.inSampleSize = calculateInSampleSize(options, 1280, 1280)
                options.inJustDecodeBounds = false
                
                val scaledBitmap = context.contentResolver.openInputStream(uri)?.use {
                    BitmapFactory.decodeStream(it, null, options)
                }
                
                //Iterative Quality Reduction
                var quality = 90
                val stream = ByteArrayOutputStream()
                scaledBitmap?.let { bitmap -> 
                    do {
                        stream.reset()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
                        quality -= 10
                    }while (stream.size() > maxSize && quality > 10)
                    
                    file.writeBytes(stream.toByteArray())
                    bitmap.recycle()
                }
                
            }

            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
            
            val result = authRepository.uploadProfilePicture(body)

            withContext(Dispatchers.Main) {
                _userState.update { it.copy(
                    name = result.name,
                    email = result.email,
                    profileImage = result.profilePic
                )}
                Log.d("ProfileViewModel", "uploadImage: $result")
            }
            
            _userState.update { it.copy(isLoading = false) }
        }
    }
    
    // helper method to calculate scaling ratio
    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.outHeight to options.outWidth
        
        var inSampleSize =1
        if (height > reqHeight || width > reqWidth){
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth){
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
    
    init {
        getUserDetails()
    }
    
    fun getUserDetails(){
        viewModelScope.launch { 
            val result = authRepository.getUserDetails()
            _userState.update { it.copy(name = result.name, email = result.email, profileImage = result.profilePic) }
            Log.d("ProfileViewModel", "getUserDetails: $result")
        }
    }
}