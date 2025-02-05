package com.arcx.saha.models

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcx.saha.MainActivity
import com.arcx.saha.data.AuthenticationRepository
import com.arcx.saha.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authenticationRepository: AuthenticationRepository
) : ViewModel() {

    var name by mutableStateOf("")
        private set
    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    fun onNameChange(content: String) {
        name = content
    }

    fun onEmailChange(content: String) {
        email = content
    }

    fun onPasswordChange(content: String) {
        password = content
    }

    fun updateEmail() {
        viewModelScope.launch {
            userRepository.emailChange(email)
        }
    }

    fun onSignUp() {
        viewModelScope.launch {
            authenticationRepository.signUp(name, email, password)
        }
    }

    fun onSignIn(context: Context) {
        viewModelScope.launch {
            authenticationRepository.signIn(email, password)
        }
        context.startActivity(Intent(context, MainActivity::class.java).apply { })
    }
}