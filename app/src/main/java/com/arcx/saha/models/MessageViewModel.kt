package com.arcx.saha.models

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcx.saha.R
import com.arcx.saha.data.Message
import com.arcx.saha.data.MessageDto
import com.arcx.saha.data.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val realtime: Realtime
) : ViewModel() {

    private val _messageList = MutableStateFlow<List<Message>>(listOf())
    val messageList: Flow<List<Message>> = _messageList

    var newMessageContent by mutableStateOf("")
        private set

    var userName by mutableStateOf("")
        private set

    private fun getUserName() {
        viewModelScope.launch {
            userName = messageRepository.getUserName()
        }
    }

    init {
        getUserName()
        realtime()
        getAllMessages()
    }

    private fun getAllMessages() {
        viewModelScope.launch {
            val messages = messageRepository.getAllMessages().reversed()
            _messageList.emit(messages.map { it.asDomainModel() })
        }
    }

    fun newMessage(content: String) {
        newMessageContent = content
    }

    fun createMedia(image: ByteArray) {
        viewModelScope.launch {
            messageRepository.createMedia(
                username = userName,
                imageName = image.toString().removePrefix("["),
                imageFile = image
            )
        }
    }

    @SuppressLint("MissingPermission", "NewApi", "NotificationPermission")
    fun notification(context: Context, message: Message) {
        val notification = NotificationCompat.Builder(context, "channel_id")
            .setContentTitle(message.username)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentText(message.content)
            .build()

        NotificationManagerCompat.from(context).notify(15, notification)
    }

    fun createMessage() {
        viewModelScope.launch {
            messageRepository.createMessage(newMessageContent, username = userName)
            getAllMessages()
        }
        newMessage("")
    }

    fun deleteMessage(id: Int, imageName: String?) {
        viewModelScope.launch {
            messageRepository.deleteMessage(id, imageName)
            getAllMessages()
        }
    }

    private fun realtime() {
        val channel = realtime.channel("messages")
        val channelFlow = channel.postgresChangeFlow<PostgresAction>(schema = "public")
        channelFlow.onEach {
            when (it) {
                is PostgresAction.Delete -> {
                    getAllMessages()
                }

                is PostgresAction.Insert -> {
                    getAllMessages()
                }

                is PostgresAction.Select -> {
                    getAllMessages()
                }

                is PostgresAction.Update -> {
                    getAllMessages()
                }
            }
        }.launchIn(viewModelScope)
        viewModelScope.launch(Dispatchers.IO) {
            channel.subscribe()
        }
    }

    private fun MessageDto.asDomainModel() = Message(
        id = id,
        content = content,
        username = username,
        image = image,
        userImage = userImage
    )
}