package com.arcx.saha.ui.screens

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.arcx.saha.data.Message
import com.arcx.saha.models.MessageViewModel

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val viewModel: MessageViewModel = hiltViewModel()
    val messages = viewModel.messageList.collectAsState(listOf()).value

    val contentResolver = LocalContext.current.contentResolver

    val pickMultipleMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(25)) { uris ->
            uris.forEach {
                urlToByteArray(contentResolver, it)?.let { it1 -> viewModel.createMedia(it1) }
            }
        }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f),
            reverseLayout = true
        ) {
            items(messages) { message ->
                SingleMessage(message = message, viewModel = viewModel)
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, bottom = 8.dp)
        ) {
            TextField(value = viewModel.newMessageContent,
                onValueChange = { viewModel.newMessage(it) },
                modifier = Modifier.fillMaxWidth(0.89f),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                trailingIcon = {
                    IconButton(onClick = {
                        pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }) {
                        Icon(Icons.Filled.PhotoLibrary, null)
                    }
                }
            )
            Spacer(Modifier.width(4.dp))
            FilledIconButton(onClick = {
                viewModel.createMessage()

            }, enabled = viewModel.newMessageContent != "") {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
            }
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SingleMessage(modifier: Modifier = Modifier, message: Message, viewModel: MessageViewModel) {

    var longPress by remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier
            .padding(horizontal = 2.dp, vertical = 4.dp)
            .sizeIn(minWidth = 32.dp)
            .fillMaxWidth()
            .wrapContentWidth(
                if (message.username == viewModel.userName) Alignment.End
                else Alignment.Start
            )
            .combinedClickable(onClick = {},
                onLongClick = {
                    longPress = !longPress
                })
    ) {
        val maxWidth = maxWidth
        val maxHeight = maxHeight
//        AsyncImage("", contentDescription = null, modifier = Modifier.size(maxWidth / 4f, height = maxHeight / 4f).clip(CircleShape))
        viewModel.notification(LocalContext.current, message)

        ElevatedCard(
            modifier = Modifier.sizeIn(maxWidth = maxWidth / 1.25f),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp,
            ),
            shape = CardDefaults.outlinedShape
        ) {
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                if (message.username != viewModel.userName) {
                    Text(
                        "~ ${message.username}",
                        fontWeight = FontWeight.W500,
                        modifier = Modifier.padding(
                            start = 6.dp,
                            top = 6.dp,
                            end = 6.dp,
                            bottom = 0.dp
                        ), style = TextStyle(color = Color.Red)
                    )
                }
                if (message.content != "") {
                    message.content?.let {
                        Text(
                            it,
                            modifier = Modifier.padding(6.dp),
                            fontSize = 18.sp
                        )
                    }
                } else {
                    AsyncImage(
                        message.image, null,
                        modifier = Modifier
                            .sizeIn(maxWidth = 256.dp, maxHeight = 320.dp)
                            .padding(3.dp)
                            .clip(
                                RoundedCornerShape(corner = CornerSize(12.dp))
                            ),
                        contentScale = ContentScale.Inside
                    )
                }
            }
        }


        if (longPress) {
            LongClick({ longPress = !longPress }, message, viewModel)
        }


    }
}

@Composable
fun LongClick(
    onClose: () -> Unit,
    message: Message,
    viewModel: MessageViewModel,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(Color.White)
            .pointerInput(onClose) { detectTapGestures { onClose() } }
    ) {
        IconButton(onClick = {
            viewModel.deleteMessage(message.id, message.image)
            onClose()
        }) {
            Icon(Icons.Filled.DeleteForever, null)
        }
    }
}

fun urlToByteArray(contentResolver: ContentResolver, uri: Uri): ByteArray? {
    return contentResolver.openInputStream(uri)?.use { it.buffered().readBytes() }
}