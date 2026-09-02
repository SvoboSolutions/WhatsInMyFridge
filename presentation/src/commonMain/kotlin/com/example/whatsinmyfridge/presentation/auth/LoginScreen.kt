package com.example.whatsinmyfridge.presentation.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.whatsinmyfridge.core.theme.FridgePillShape
import com.example.whatsinmyfridge.core.theme.FridgeSpacing
import com.mmk.kmpauth.apple.rememberAppleAuthState
import com.mmk.kmpauth.core.auth.EmailAuthMode
import com.mmk.kmpauth.core.auth.rememberEmailAuthState
import com.mmk.kmpauth.google.rememberGoogleAuthState
import com.mmk.kmpauth.uihelper.apple.AppleSignInButton
import com.mmk.kmpauth.uihelper.google.GoogleSignInButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen() {
    var mode by remember { mutableStateOf(EmailAuthMode.SignIn) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    var pendingError by remember { mutableStateOf<String?>(null) }

    val googleAuth = rememberGoogleAuthState(onResult = { result ->
        result.onFailure { error -> pendingError = error.message ?: "Anmeldung fehlgeschlagen" }
    })
    val emailAuth = rememberEmailAuthState(
        email = email,
        password = password,
        mode = mode,
        onResult = { result ->
            result.onFailure { error -> pendingError = error.message ?: "Anmeldung fehlgeschlagen" }
        },
    )
    val appleAuth = rememberAppleAuthState(onResult = { result ->
        result.onFailure { error -> pendingError = error.message ?: "Anmeldung fehlgeschlagen" }
    })

    LaunchedEffect(pendingError) {
        pendingError?.let {
            snackbarHostState.showSnackbar(it)
            pendingError = null
        }
    }

    val isBusy = googleAuth.isInProgress || emailAuth.isInProgress || appleAuth.isInProgress

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = FridgeSpacing.lg),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(96.dp),
            ) {
                Icon(
                    Icons.Filled.Kitchen,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(FridgeSpacing.md),
                )
            }

            Text(
                "WhatsInMyFridge",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = FridgeSpacing.md),
            )
            Text(
                "Zutaten eingeben, passende Rezepte finden",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = FridgeSpacing.xs, bottom = FridgeSpacing.xl),
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("E-Mail") },
                leadingIcon = { Icon(Icons.Filled.Mail, contentDescription = null) },
                singleLine = true,
                shape = MaterialTheme.shapes.large,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Passwort") },
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = null,
                        )
                    }
                },
                singleLine = true,
                shape = MaterialTheme.shapes.large,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth().padding(top = FridgeSpacing.sm),
            )

            Button(
                onClick = { emailAuth.launch() },
                enabled = !isBusy && email.isNotBlank() && password.length >= 6,
                shape = FridgePillShape,
                modifier = Modifier.padding(top = FridgeSpacing.md).fillMaxWidth().height(52.dp),
            ) {
                if (emailAuth.isInProgress) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Text(if (mode == EmailAuthMode.SignIn) "Anmelden" else "Konto erstellen")
                }
            }

            TextButton(onClick = {
                mode = if (mode == EmailAuthMode.SignIn) EmailAuthMode.SignUp else EmailAuthMode.SignIn
            }) {
                Text(
                    if (mode == EmailAuthMode.SignIn) "Noch kein Konto? Registrieren"
                    else "Schon ein Konto? Anmelden",
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(vertical = FridgeSpacing.md),
            ) {
                HorizontalDivider(Modifier.weight(1f))
                Text(
                    "oder",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(horizontal = FridgeSpacing.smMd),
                )
                HorizontalDivider(Modifier.weight(1f))
            }

            if (googleAuth.isInProgress) {
                CircularProgressIndicator()
            } else {
                GoogleSignInButton(
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = FridgePillShape,
                    onClick = { googleAuth.launch() },
                )
            }

            if (appleAuth.isInProgress) {
                CircularProgressIndicator(modifier = Modifier.padding(top = FridgeSpacing.sm))
            } else {
                AppleSignInButton(
                    modifier = Modifier.padding(top = FridgeSpacing.sm).fillMaxWidth().height(52.dp),
                    shape = FridgePillShape,
                    onClick = { appleAuth.launch() },
                )
            }

            Spacer(modifier = Modifier.height(FridgeSpacing.lg))
        }
    }
}
