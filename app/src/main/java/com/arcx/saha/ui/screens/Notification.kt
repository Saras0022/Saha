package com.arcx.saha.ui.screens

import com.arcx.saha.data.MessageRepository
import javax.inject.Inject

class Notification @Inject constructor(
    private val messageRepository: MessageRepository
) {
    init {

    }
}