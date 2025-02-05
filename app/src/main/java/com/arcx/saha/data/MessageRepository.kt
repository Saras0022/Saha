package com.arcx.saha.data


interface MessageRepository {
    suspend fun createMessage(content: String, username: String): Boolean
    suspend fun getAllMessages(): List<MessageDto>
    suspend fun getMessage(id: Int): MessageDto
    suspend fun deleteMessage(id: Int, imageName: String?)
    suspend fun updateMessage(id: Int, content: String)
    suspend fun getUserName(): String
    suspend fun createMedia(imageName: String, imageFile: ByteArray, username: String)
}