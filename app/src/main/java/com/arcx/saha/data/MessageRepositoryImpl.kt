package com.arcx.saha.data

import com.arcx.saha.BuildConfig
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    private val auth: Auth, private val postgrest: Postgrest, private val storage: Storage
) : MessageRepository {
    override suspend fun createMessage(content: String, username: String): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val messageDto = MessageDto(
                    content = content,
                    username = username
                )
                postgrest.from("messages").insert(messageDto)
                true
            }
            true
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getAllMessages(): List<MessageDto> {
        return withContext(Dispatchers.IO) {
            val result = postgrest.from("messages").select().decodeList<MessageDto>()
            result
        }
    }

    override suspend fun getMessage(id: Int): MessageDto {
        TODO("Not yet implemented")
    }

    override suspend fun deleteMessage(id: Int, imageName: String?) {
        withContext(Dispatchers.IO) {
            postgrest.from("messages").delete {
                filter {
                    eq("id", id)
                }
            }
            if (imageName != null) {
                storage.from("images").delete(getImageName(imageName))
            }
        }
    }

    override suspend fun updateMessage(id: Int, content: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getUserName(): String {
        return withContext(Dispatchers.IO) {
            val userName =
                auth.currentUserOrNull()?.userMetadata?.get("username").toString().trim('"')
            userName
        }
    }

    override suspend fun createMedia(imageName: String, imageFile: ByteArray, username: String) {
        withContext(Dispatchers.IO) {
            storage.from("images").upload(
                path = "$imageName.png",
                data = imageFile
            )
            val messageDto = MessageDto(
                content = "",
                username = username,
                image = buildUrl(imageName)
            )
            postgrest.from("messages").insert(messageDto)
        }
    }

    private fun getImageName(imageName: String) =
        imageName.replaceBeforeLast('/', "").trim('/')

    private fun buildUrl(imageFileName: String) =
        "${BuildConfig.SUPABASE_URL}/storage/v1/object/public/images/${imageFileName}.png"
}