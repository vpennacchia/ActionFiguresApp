package com.example.actionfiguresapp.android.ui.social

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.actionfiguresapp.android.DarkPanel2
import com.example.actionfiguresapp.android.GridLine
import com.example.actionfiguresapp.android.NeonCyan
import com.example.actionfiguresapp.android.NeonGold
import com.example.actionfiguresapp.android.SpaceBlack
import com.example.actionfiguresapp.android.TextSecondary
import com.example.actionfiguresapp.domain.model.ActionFigure
import com.example.actionfiguresapp.presentation.viewmodel.PublicCollectionDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicCollectionDetailScreen(
    userId: String,
    collectionId: String,
    collectionName: String,
    viewModel: PublicCollectionDetailViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(userId, collectionId) {
        viewModel.load(userId, collectionId, collectionName)
    }

    Scaffold(
        containerColor = SpaceBlack,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            uiState.collectionName.uppercase(),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = NeonCyan,
                            letterSpacing = 1.sp
                        )
                        Text(
                            "COLLECTION.VIEW // READ ONLY",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = NeonCyan)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SpaceBlack)
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator(color = NeonCyan) }

            uiState.figures.isEmpty() -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "[ DATABASE VUOTO ]",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    letterSpacing = 2.sp
                )
            }

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(uiState.figures, key = { it.id }) { figure ->
                    PublicFigureCard(figure = figure)
                }
            }
        }
    }
}

@Composable
private fun PublicFigureCard(figure: ActionFigure) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(DarkPanel2)
            .border(1.dp, GridLine, MaterialTheme.shapes.medium)
    ) {
        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(SpaceBlack)
                    .border(1.dp, GridLine, MaterialTheme.shapes.small),
                contentAlignment = Alignment.Center
            ) {
                if (figure.imageUrl != null) {
                    AsyncImage(
                        model = figure.imageUrl,
                        contentDescription = figure.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(Icons.Default.SmartToy, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(32.dp))
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    figure.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    color = Color.White
                )
                figure.condition?.let {
                    Spacer(Modifier.height(2.dp))
                    Text(it.uppercase(), fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = NeonCyan.copy(0.6f), letterSpacing = 1.sp)
                }
                figure.price?.let {
                    Spacer(Modifier.height(4.dp))
                    Text("€ ${"%.2f".format(it)}", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NeonGold)
                }
            }
        }
    }
}
