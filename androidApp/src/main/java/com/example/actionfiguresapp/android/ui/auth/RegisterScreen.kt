package com.example.actionfiguresapp.android.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.actionfiguresapp.android.DarkPanel2
import com.example.actionfiguresapp.android.GridLine
import com.example.actionfiguresapp.android.NeonCyan
import com.example.actionfiguresapp.android.NeonGold
import com.example.actionfiguresapp.android.NeonGreen
import com.example.actionfiguresapp.android.NeonPurple
import com.example.actionfiguresapp.android.SpaceBlack
import com.example.actionfiguresapp.android.TextSecondary
import com.example.actionfiguresapp.presentation.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var displayName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(uiState.user) { if (uiState.user != null) onRegisterSuccess() }
    LaunchedEffect(uiState.error) {
        uiState.error?.let { snackbarHostState.showSnackbar(it); viewModel.clearError() }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = SpaceBlack
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Brush.radialGradient(colors = listOf(Color(0xFF0D0A1E), SpaceBlack), radius = 1200f))
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("NEW_PLAYER.INIT()", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 18.sp, letterSpacing = 2.sp, color = NeonGreen)
                Spacer(Modifier.height(4.dp))
                Text("CREA IL TUO PROFILO COLLEZIONISTA", fontFamily = FontFamily.Monospace, fontSize = 9.sp, letterSpacing = 1.5.sp, color = TextSecondary)

                Spacer(Modifier.height(32.dp))

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Box(Modifier.weight(1f).height(1.dp).background(GridLine))
                    Text("  REGISTRAZIONE  ", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TextSecondary, letterSpacing = 2.sp)
                    Box(Modifier.weight(1f).height(1.dp).background(GridLine))
                }

                Spacer(Modifier.height(24.dp))

                listOf(
                    Triple(displayName, { v: String -> displayName = v }, "USERNAME"),
                    Triple(email, { v: String -> email = v }, "EMAIL"),
                ).forEachIndexed { index, (value, onValue, label) ->
                    val accentColor = if (index == 0) NeonGreen else NeonCyan
                    OutlinedTextField(
                        value = value,
                        onValueChange = onValue,
                        label = { Text(label, fontFamily = FontFamily.Monospace, fontSize = 11.sp) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = if (label == "EMAIL") KeyboardType.Email else KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentColor,
                            unfocusedBorderColor = GridLine,
                            focusedLabelColor = accentColor,
                            unfocusedLabelColor = TextSecondary,
                            focusedTextColor = accentColor,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            cursorColor = accentColor
                        ),
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))
                }

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("PASSWORD", fontFamily = FontFamily.Monospace, fontSize = 11.sp) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonPurple,
                        unfocusedBorderColor = GridLine,
                        focusedLabelColor = NeonPurple,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = NeonPurple,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        cursorColor = NeonPurple
                    ),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(28.dp))

                Button(
                    onClick = { viewModel.signUp(email, password, displayName) },
                    enabled = !uiState.isLoading && displayName.isNotBlank() && email.isNotBlank() && password.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = MaterialTheme.shapes.small,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = SpaceBlack, disabledContainerColor = GridLine)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(color = SpaceBlack, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Text("[ CREA ACCOUNT ]", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    }
                }

                Spacer(Modifier.height(12.dp))

                TextButton(onClick = onNavigateToLogin) {
                    Text("GIÀ REGISTRATO? ", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = TextSecondary)
                    Text("LOGIN", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = NeonGold)
                }
            }
        }
    }
}
