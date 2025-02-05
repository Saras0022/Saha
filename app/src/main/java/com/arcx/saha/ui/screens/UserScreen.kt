package com.arcx.saha.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.navigation.compose.hiltViewModel
import com.arcx.saha.models.UserViewModel

@Composable
fun UserScreen(modifier: Modifier = Modifier, viewModel: UserViewModel = hiltViewModel()) {

    val context = LocalContext.current
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        SignUpScreen(viewModel)
        Button(onClick = { viewModel.onSignIn(context = context) }) {
            Text("SignIn")
        }
    }
}

@Composable
fun SignUpScreen(viewModel: UserViewModel) {
    val focusManager = LocalFocusManager.current
    Card(onClick = {}) {
        OutlinedTextField(
            value = viewModel.name, onValueChange = { viewModel.onNameChange(it) },
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            singleLine = true,
        )
        OutlinedTextField(
            value = viewModel.email, onValueChange = { viewModel.onEmailChange(it) },
            keyboardActions = KeyboardActions(onDone = { viewModel.updateEmail() }),
            singleLine = true
        )
        OutlinedTextField(
            value = viewModel.password,
            onValueChange = { viewModel.onPasswordChange(it) })
        Button(onClick = { viewModel.onSignUp() }) {
            Text("SignIn/SignUp")
        }
    }
}