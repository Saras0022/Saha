package com.arcx.saha.data

interface UserRepository {
    suspend fun userNameChange(name: String)
    suspend fun emailChange(newEmail: String)
    suspend fun phoneChange(newPhone: String)
    suspend fun getUserName(): String
}