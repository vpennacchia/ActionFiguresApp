package com.example.actionfiguresapp.android.ui.social

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.actionfiguresapp.android.DarkPanel
import com.example.actionfiguresapp.android.GridLine
import com.example.actionfiguresapp.android.NeonCyan
import com.example.actionfiguresapp.android.NeonGold
import com.example.actionfiguresapp.android.NeonPurple
import com.example.actionfiguresapp.android.SpaceBlack
import com.example.actionfiguresapp.android.TextSecondary
import com.example.actionfiguresapp.domain.model.Collection
import com.example.actionfiguresapp.presentation.viewmodel.PublicProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicProfileScreen(
    userId: String,
    publicProfileViewModel: PublicProfileViewModel,
    onCollectionClick: (collectionId: String, collectionName: String) -> Unit,
    onBack: () -> Unit
) {
    val profileState by publicProfileViewModel.uiState.collectAsState()

    LaunchedEffect(userId) {
        publicProfileViewModel.loadProfile(userId)
    }

    Scaffold(
        containerColor = SpaceBlack,
        topBar = {
            TopAppBar(
                title = {
                    val name = profileState.profile?.displayName ?: "PROFILO"
                    Text(name.uppercase(), fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = NeonCyan)
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
        when {
            profileState.isLoading && profileState.profile == null ->
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = NeonCyan)
                }
            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(Modifier.height(12.dp))
                profileState.profile?.let { profile ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkPanel, MaterialTheme.shapes.medium)
                            .border(1.dp, NeonPurple.copy(0.4f), MaterialTheme.shapes.medium)
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(SpaceBlack)
                                    .border(2.dp, NeonPurple, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = profile.displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp,
                                    color = NeonPurple
                                )
                            }
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text(profile.displayName, style = MaterialTheme.typography.headlineSmall, color = Color.White)
                                if (profile.bio.isNotBlank()) {
                                    Spacer(Modifier.height(4.dp))
                                    Text(profile.bio, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                }
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    "${profile.collectionsCount} collezioni",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = NeonCyan.copy(0.8f)
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                }
                Text(
                    "COLLEZIONI",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = NeonCyan,
                    letterSpacing = 2.sp
                )
                Spacer(Modifier.height(10.dp))
                if (profileState.collections.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 24.dp), contentAlignment = Alignment.Center) {
                        Text("[ NESSUNA COLLEZIONE ]", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = TextSecondary, letterSpacing = 1.sp)
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(profileState.collections) { collection ->
                            PublicCollectionCard(
                                collection = collection,
                                onClick = { onCollectionClick(collection.id, collection.name) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PublicCollectionCard(collection: Collection, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(
                Brush.linearGradient(listOf(Color(0xFF003D52), Color(0xFF001A2E))),
                MaterialTheme.shapes.medium
            )
            .border(1.dp, NeonCyan.copy(alpha = 0.4f), MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(NeonCyan).align(Alignment.TopStart))
        Column(
            modifier = Modifier.fillMaxSize().padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.SmartToy, contentDescription = null, tint = NeonCyan.copy(0.5f), modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text("SYS://COL", fontFamily = FontFamily.Monospace, fontSize = 8.sp, color = NeonCyan.copy(0.4f), letterSpacing = 1.sp)
            }
            Column {
                Text(text = collection.name, style = MaterialTheme.typography.bodyMedium, color = Color.White, maxLines = 2)
                Spacer(Modifier.height(4.dp))
                Text(
                    "${collection.figureCount}x figure",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = NeonGold.copy(0.8f)
                )
            }
        }
    }
}
