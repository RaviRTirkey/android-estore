package com.learning.e_store.core.session

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionEventManager @Inject constructor() {
    private val _sessionExpired = MutableSharedFlow<Unit>()
    val sessionExpired = _sessionExpired.asSharedFlow()
    
    suspend fun notifySessionExpired(){
        _sessionExpired.emit(Unit)
    }
}