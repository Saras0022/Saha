package com.arcx.saha.data

import kotlinx.serialization.Serializable

@Serializable
data class MessageDto(
    val id: Int = 0,
    val content: String?,
    val username: String,
    val image: String? = null,
    val userImage: String? = null
)