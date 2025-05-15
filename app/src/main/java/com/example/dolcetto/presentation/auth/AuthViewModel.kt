package com.example.dolcetto.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dolcetto.data.repository.AuthRepository
import com.example.dolcetto.data.repository.Resource
import com.example.dolcetto.domain.model.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<Resource<UserData>?>(null)
    val loginState: StateFlow<Resource<UserData>?> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<Resource<UserData>?>(null)
    val registerState: StateFlow<Resource<UserData>?> = _registerState.asStateFlow()

    private val _currentUser = MutableStateFlow<UserData?>(null)
    val currentUser: StateFlow<UserData?> = _currentUser.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        observeAuthState()
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Resource.Loading()
            _isLoading.value = true
            val result = authRepository.login(email, password)
            _loginState.value = result
            _isLoading.value = false
            result.data?.let { _currentUser.value = it }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _registerState.value = Resource.Loading()
            _isLoading.value = true
            val result = authRepository.register(email, password)
            _registerState.value = result
            _isLoading.value = false
            result.data?.let { _currentUser.value = it }
        }
    }

    fun logout() {
        authRepository.logout()
        _currentUser.value = null
    }

    private fun observeAuthState() {
        authRepository.firebaseAuth.addAuthStateListener { auth ->
            viewModelScope.launch {
                _isLoading.value = true
                if (auth.currentUser != null) {
                    val result = authRepository.fetchUserData(auth.currentUser!!.uid)
                    if (result is Resource.Success) {
                        _currentUser.value = result.data
                    }
                } else {
                    _currentUser.value = null
                }
                _isLoading.value = false
            }
        }
    }

}