package com.arcx.saha.data

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class UserRepositoryImpl @Inject constructor(
    private val auth: Auth, private val postgrest: Postgrest
) : UserRepository {

    private val email = auth.currentSessionOrNull()?.user?.email

    override suspend fun userNameChange(name: String) {
        withContext(Dispatchers.IO) {
            if (email != null) {
                postgrest.from("profiles").update(
                    {
                        set("username", name)
                    }
                ) {
                    filter {
                        eq("email", email)
                    }
                }
            }
            println(
                postgrest.from(schema = "public", "profiles").select(Columns.raw("username")).data
            )
        }
    }

    override suspend fun emailChange(newEmail: String) {
        withContext(Dispatchers.IO) {
            auth.updateUser {
                email = newEmail
            }
        }
    }

    override suspend fun phoneChange(newPhone: String) {
        withContext(Dispatchers.IO) {
            auth.updateUser {
                phone = newPhone
            }
        }
    }

    override suspend fun getUserName(): String {
        val data: String
        withContext(Dispatchers.IO) {
            data = auth.currentSessionOrNull()?.user?.userMetadata?.get("username").toString()
                .trim('"')
        }
        return data
    }
}