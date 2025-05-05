package com.example.fitnessapp.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController


@Composable
fun AuthScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val passwordFocusRequester = remember { FocusRequester() }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Email
            ),
            keyboardActions = KeyboardActions(
                onNext = { passwordFocusRequester.requestFocus() }
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Şifre") },
            modifier = Modifier.focusRequester(passwordFocusRequester),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Default.Visibility
                else
                    Icons.Default.VisibilityOff

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = if (passwordVisible) "Şifreyi gizle" else "Şifreyi göster")
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Number
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (email.isBlank() || password.isBlank()) {
                        message = "Email ve şifre boş bırakılamaz."
                    } else if (!isValidEmail(email)) {
                        message = "Geçerli bir email adresi girin."
                    } else {
                        authViewModel.loginUser(
                            email,
                            password,
                            onSuccess = {
                                navController.navigate("home") {
                                    popUpTo("auth") { inclusive = true }
                                }
                            },
                            onError = { error -> message = error }
                        )
                        focusManager.clearFocus()
                    }
                }
            )

        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            when {
                email.isBlank() || password.isBlank() -> {
                    message = "Email ve şifre boş bırakılamaz."
                }
                !isValidEmail(email) -> {
                    message = "Geçerli bir email adresi girin."
                }
                !isValidPassword(password) -> {
                    message = "Şifre en az 6 karakter olmalı."
                }
                else -> {
                    authViewModel.registerUser(
                        email,
                        password,
                        onSuccess = {
                            navController.navigate("home") {
                                popUpTo("auth") { inclusive = true }
                            }
                        },
                        onError = { error -> message = error }
                    )
                }
            }
        }) {
            Text("Kayıt Ol")
        }



        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            when {
                email.isBlank() || password.isBlank() -> {
                    message = "Email ve şifre boş bırakılamaz."
                }
                !isValidEmail(email) -> {
                    message = "Geçerli bir email adresi girin."
                }
                else -> {
                    authViewModel.loginUser(
                        email,
                        password,
                        onSuccess = {
                            navController.navigate("home") {
                                popUpTo("auth") { inclusive = true }
                            }
                        },
                        onError = { error -> message = error }
                    )
                }
            }
        }) {
            Text("Giriş Yap")
        }



        Spacer(modifier = Modifier.height(16.dp))

        Text(text = message)
    }
}

fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}


fun isValidPassword(password: String): Boolean {
    return password.length >= 6
}
