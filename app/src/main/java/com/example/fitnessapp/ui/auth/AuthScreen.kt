package com.example.fitnessapp.ui.auth

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.fitnessapp.R
import com.example.fitnessapp.ui.auth.GoogleAuthUiClient
import com.example.fitnessapp.ui.theme.FitnessAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun AuthScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val passwordFocusRequester = remember { FocusRequester() }
    // Google girisi degiskenleri
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data ?: return@rememberLauncherForActivityResult
                GoogleAuthUiClient(context).signInWithIntent(intent) { success, error ->
                    if (success) {
                        navController.navigate("home") {
                            popUpTo("auth") { inclusive = true }
                        }
                    } else {
                        Toast.makeText(context, error ?: "Giriş hatası", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    )


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .size(240.dp)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

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
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Şifre") },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(passwordFocusRequester),
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
                                navController?.navigate("home") {
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

        Button(
            onClick = {
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
                            email = email,
                            password = password,
                            onSuccess = {
                                navController.navigate("profile_setup") {
                                    popUpTo("auth") { inclusive = true }
                                }
                            },
                            onError = { error ->
                                message = error
                            }
                        )
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Kayıt Ol", color = Color.White)
        }




        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
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
                                navController?.navigate("home") {
                                    popUpTo("auth") { inclusive = true }
                                }
                            },
                            onError = { error -> message = error }
                        )
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Giriş Yap", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            CoroutineScope(Dispatchers.Main).launch {
                val intentSender = GoogleAuthUiClient(context).signIn()
                intentSender?.let {
                    launcher.launch(
                        IntentSenderRequest.Builder(it).build()
                    )
                }
            }
        }) {
            Text("Google ile Giriş Yap")
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


