package com.arcx.saha

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.arcx.saha.ui.screens.MainScreen
import com.arcx.saha.ui.screens.UserScreen
import com.arcx.saha.ui.theme.SahaTheme
import dagger.hilt.android.AndroidEntryPoint
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var supabaseClient: SupabaseClient

    val scope = CoroutineScope(Dispatchers.IO)
    val context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "channel_id",
                "channel_name",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        setContent {
            SahaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(modifier = Modifier.padding(innerPadding)) {
                        LaunchedEffect(supabaseClient.auth.currentSessionOrNull()) {
                        }

                        if (supabaseClient.auth.currentSessionOrNull() == null) {
                            UserScreen()
                        } else {
                            MainScreen()
                        }
                    }
                }
            }
        }

//        scope.launch {
//            supabaseClient.auth.sessionStatus.collect {
//                when(it) {
//                    is SessionStatus.Authenticated ->
//                        startActivity(Intent(context, MessageActivity::class.java).apply {
//                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                    })
//                    SessionStatus.Initializing -> TODO()
//                    is SessionStatus.NotAuthenticated -> startActivity(Intent(context, SignedActivity::class.java).apply {
//                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                    })
//                    is SessionStatus.RefreshFailure -> TODO()
//                }
//            }
//        }
    }
}