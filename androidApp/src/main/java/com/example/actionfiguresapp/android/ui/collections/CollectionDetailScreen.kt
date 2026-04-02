package com.example.actionfiguresapp.android.ui.collections

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import com.example.actionfiguresapp.android.NeonGreen
import com.example.actionfiguresapp.android.NeonPink
import com.example.actionfiguresapp.android.NeonPurple
import com.example.actionfiguresapp.android.SpaceBlack
import com.example.actionfiguresapp.android.TextSecondary
import com.example.actionfiguresapp.domain.model.ActionFigure
import com.example.actionfiguresapp.domain.model.Collection
import com.example.actionfiguresapp.presentation.viewmodel.CollectionsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionDetailScreen(
    collectionId: String,
    collectionsViewModel: CollectionsViewModel,
    onAddFigure: () -> Unit,
    onBack: () -> Unit
) {
    val uiState by collectionsViewModel.uiState.collectAsState()
    val collection = uiState.collections.find { it.id == collectionId }

    LaunchedEffect(collectionId) { collection?.let { collectionsViewModel.selectCollection(it) } }

    Scaffold(
        containerColor = SpaceBlack,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(collection?.name?.uppercase() ?: "", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NeonCyan, letterSpacing = 1.sp)
                        Text("COLLECTION.VIEW", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = TextSecondary, letterSpacing = 1.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = NeonCyan)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SpaceBlack)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddFigure, containerColor = NeonGreen, contentColor = SpaceBlack, shape = MaterialTheme.shapes.small) {
                Icon(Icons.Default.Add, contentDescription = "Aggiungi figura")
            }
        }
    ) { padding ->
        if (collection == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NeonCyan)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { HudValueCard(collection = collection) }
            item { Spacer(Modifier.height(4.dp)) }
            if (collection.figures.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("[ DATABASE VUOTO ]", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NeonPurple, letterSpacing = 2.sp)
                            Spacer(Modifier.height(8.dp))
                            Text("Premi + per aggiungere una figura", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                }
            } else {
                items(collection.figures, key = { it.id }) { figure ->
                    FigureCard(figure = figure, onDelete = { collectionsViewModel.removeFigure(collectionId, figure.id) })
                }
            }
        }
    }
}

@Composable
private fun HudValueCard(collection: Collection) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(Brush.linearGradient(listOf(Color(0xFF001A2E), Color(0xFF0A001A))))
            .border(1.dp, NeonCyan.copy(0.3f), MaterialTheme.shapes.medium)
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(Brush.horizontalGradient(listOf(NeonCyan, NeonPurple))).align(Alignment.TopStart))
        Column(modifier = Modifier.padding(16.dp)) {
            Text("// ANALISI VALORE COLLEZIONE", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = TextSecondary, letterSpacing = 1.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                text = "€ ${"%,.2f".format(collection.totalValue)}",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = NeonGold,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                HudStat(label = "FIGURE", value = "${collection.figureCount}", color = NeonCyan)
                if (collection.figureCount > 0) {
                    HudStat(label = "MEDIA", value = "€${"%.2f".format(collection.totalValue / collection.figureCount)}", color = NeonPurple)
                }
            }
        }
    }
}

@Composable
private fun HudStat(label: String, value: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(0.08f), MaterialTheme.shapes.small)
            .border(1.dp, color.copy(0.3f), MaterialTheme.shapes.small)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = color)
            Text(label, fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = TextSecondary, letterSpacing = 1.sp)
        }
    }
}

@Composable
private fun FigureCard(figure: ActionFigure, onDelete: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(DarkPanel2)
            .border(1.dp, GridLine, MaterialTheme.shapes.medium)
    ) {
        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(68.dp).clip(MaterialTheme.shapes.small)
                    .background(SpaceBlack).border(1.dp, GridLine, MaterialTheme.shapes.small),
                contentAlignment = Alignment.Center
            ) {
                if (figure.imageUrl != null) {
                    AsyncImage(model = figure.imageUrl, contentDescription = figure.name, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                } else {
                    Icon(Icons.Default.SmartToy, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(32.dp))
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(figure.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, maxLines = 2, color = MaterialTheme.colorScheme.onSurface)
                figure.condition?.let {
                    Spacer(Modifier.height(2.dp))
                    Text(it.uppercase(), fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = NeonCyan.copy(0.6f), letterSpacing = 1.sp)
                }
                figure.price?.let {
                    Spacer(Modifier.height(4.dp))
                    Text("€ ${"%.2f".format(it)}", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NeonGold)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Rimuovi", tint = NeonPink, modifier = Modifier.size(20.dp))
            }
        }
    }
}
