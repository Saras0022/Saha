package com.arcx.saha.data

interface AuthenticationRepository {
    suspend fun signUp(userName: String, email: String, password: String): Boolean
    suspend fun signIn(email: String, password: String): Boolean
}