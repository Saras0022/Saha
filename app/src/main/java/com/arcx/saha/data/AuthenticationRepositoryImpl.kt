package com.arcx.saha.data

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    private val auth: Auth
) : AuthenticationRepository {
    override suspend fun signUp(userName: String, email: String, password: String): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                auth.signUpWith(Email) {
                    this.data = buildJsonObject {
                        put("username", userName)
                    }
                    this.email = email
                    this.password = password
                }
            }
            true
        } catch (e: AuthRestException) {
            throw e
        }
    }

    override suspend fun signIn(email: String, password: String): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }
            }
            auth.awaitInitialization()
            true
        } catch (e: Exception) {
            throw e
        }
    }
}