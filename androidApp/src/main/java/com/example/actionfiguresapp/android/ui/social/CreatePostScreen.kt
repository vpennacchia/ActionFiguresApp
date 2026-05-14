package com.example.actionfiguresapp.android.ui.social

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.actionfiguresapp.android.DarkPanel2
import com.example.actionfiguresapp.android.GridLine
import com.example.actionfiguresapp.android.NeonCyan
import com.example.actionfiguresapp.android.NeonPurple
import com.example.actionfiguresapp.android.SpaceBlack
import com.example.actionfiguresapp.android.TextSecondary
import com.example.actionfiguresapp.presentation.viewmodel.AuthViewModel
import com.example.actionfiguresapp.presentation.viewmodel.CreatePostViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    authViewModel: AuthViewModel,
    createPostViewModel: CreatePostViewModel,
    onBack: () -> Unit
) {
    val authState by authViewModel.uiState.collectAsState()
    val postState by createPostViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                val bytes = withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)?.readBytes()
                }
                createPostViewModel.onImageSelected(bytes)
            }
        }
    }

    LaunchedEffect(postState.postSuccess) {
        if (postState.postSuccess) onBack()
    }

    Scaffold(
        containerColor = SpaceBlack,
        topBar = {
            TopAppBar(
                title = {
                    Text("NUOVO_POST", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, letterSpacing = 2.sp, color = NeonCyan)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Indietro", tint = NeonCyan)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SpaceBlack)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = postState.text,
                onValueChange = { createPostViewModel.onTextChange(it) },
                label = { Text("Scrivi qualcosa...", fontFamily = FontFamily.Monospace, fontSize = 11.sp) },
                modifier = Modifier.fillMaxWidth().height(160.dp),
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    focusedLabelColor = NeonCyan,
                    unfocusedBorderColor = GridLine,
                    unfocusedLabelColor = TextSecondary
                )
            )

            if (postState.selectedImageBytes != null) {
                AsyncImage(
                    model = postState.selectedImageBytes,
                    contentDescription = "Anteprima",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(MaterialTheme.shapes.medium)
                        .border(1.dp, NeonPurple.copy(0.5f), MaterialTheme.shapes.medium)
                )
                TextButton(onClick = { createPostViewModel.onImageSelected(null) }) {
                    Text("[ RIMUOVI IMMAGINE ]", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TextSecondary)
                }
            } else {
                TextButton(
                    onClick = { imagePicker.launch("image/*") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkPanel2, MaterialTheme.shapes.medium)
                        .border(1.dp, GridLine, MaterialTheme.shapes.medium)
                ) {
                    Icon(Icons.Default.Image, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(18.dp))
                    Text("  AGGIUNGI FOTO", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = NeonPurple)
                }
            }

            postState.error?.let { error ->
                Text(error, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    authState.user?.let { user ->
                        createPostViewModel.submitPost(
                            authorId = user.uid,
                            authorName = user.displayName ?: user.email,
                            authorPhotoUrl = user.photoUrl
                        )
                    }
                },
                enabled = postState.text.isNotBlank() && !postState.isLoading,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = SpaceBlack)
            ) {
                if (postState.isLoading) {
                    CircularProgressIndicator(color = SpaceBlack, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text("[ PUBBLICA ]", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
            }
        }
    }
}
