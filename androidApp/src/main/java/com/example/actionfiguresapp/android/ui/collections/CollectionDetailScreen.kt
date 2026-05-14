package com.example.actionfiguresapp.android.ui.collections

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.actionfiguresapp.presentation.viewmodel.PriceAlert

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

    LaunchedEffect(collectionId) {
        collection?.let { collectionsViewModel.selectCollection(it) }
    }

    // Avvia il refresh dei prezzi quando si apre la schermata
    LaunchedEffect(collectionId, collection != null) {
        if (collection != null) {
            collectionsViewModel.refreshCollectionPrices(collectionId)
        }
    }

    Scaffold(
        containerColor = SpaceBlack,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            collection?.name?.uppercase() ?: "",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = NeonCyan,
                            letterSpacing = 1.sp
                        )
                        Text(
                            "COLLECTION.VIEW",
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddFigure,
                containerColor = NeonGreen,
                contentColor = SpaceBlack,
                shape = MaterialTheme.shapes.small
            ) {
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

            // Banner refresh in corso
            if (uiState.isRefreshingPrices) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .clip(MaterialTheme.shapes.small)
                            .background(NeonCyan.copy(0.05f))
                            .border(1.dp, NeonCyan.copy(0.2f), MaterialTheme.shapes.small)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        LinearProgressIndicator(
                            modifier = Modifier.weight(1f).height(2.dp),
                            color = NeonCyan,
                            trackColor = NeonCyan.copy(0.1f)
                        )
                        Text(
                            "AGGIORNAMENTO PREZZI...",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            color = NeonCyan,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            // Banner alert prezzi (dismissibile)
            if (uiState.priceAlerts.isNotEmpty()) {
                item {
                    PriceAlertsCard(
                        alerts = uiState.priceAlerts,
                        onDismiss = { collectionsViewModel.dismissPriceAlerts() }
                    )
                }
            }

            item { Spacer(Modifier.height(4.dp)) }

            if (collection.figures.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "[ DATABASE VUOTO ]",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = NeonPurple,
                                letterSpacing = 2.sp
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Premi + per aggiungere una figura",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            } else {
                items(collection.figures, key = { it.id }) { figure ->
                    val alert = uiState.priceAlerts.find { it.figureId == figure.id }
                    FigureCard(
                        figure = figure,
                        priceAlert = alert,
                        onDelete = { collectionsViewModel.removeFigure(collectionId, figure.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PriceAlertsCard(alerts: List<PriceAlert>, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(Brush.linearGradient(listOf(Color(0xFF1A0D00), Color(0xFF001A0D))))
            .border(1.dp, NeonGold.copy(0.5f), MaterialTheme.shapes.medium)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().height(2.dp)
                .background(Brush.horizontalGradient(listOf(NeonGold, NeonGreen)))
                .align(Alignment.TopStart)
        )
        Column(modifier = Modifier.padding(12.dp).padding(top = 4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "// VARIAZIONI PREZZO RILEVATE",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    color = NeonGold,
                    letterSpacing = 1.sp
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(20.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Chiudi", tint = TextSecondary, modifier = Modifier.size(14.dp))
                }
            }
            Spacer(Modifier.height(8.dp))
            alerts.forEach { alert ->
                PriceAlertRow(alert = alert)
                Spacer(Modifier.height(6.dp))
            }
        }
    }
}

@Composable
private fun PriceAlertRow(alert: PriceAlert) {
    val trendColor = if (alert.isUp) NeonGreen else NeonPink
    val trendIcon = if (alert.isUp) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown
    val sign = if (alert.isUp) "+" else ""

    Row(
        modifier = Modifier.fillMaxWidth()
            .background(trendColor.copy(0.05f), MaterialTheme.shapes.small)
            .border(1.dp, trendColor.copy(0.2f), MaterialTheme.shapes.small)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(trendIcon, contentDescription = null, tint = trendColor, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(8.dp))
        Text(
            text = alert.figureName,
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
            maxLines = 1
        )
        Spacer(Modifier.width(8.dp))
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "€${"%.2f".format(alert.newPrice)}",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = trendColor
            )
            Text(
                text = "$sign${"%.1f".format(alert.changePercent)}%",
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                color = trendColor.copy(0.7f)
            )
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
        Box(
            modifier = Modifier.fillMaxWidth().height(2.dp)
                .background(Brush.horizontalGradient(listOf(NeonCyan, NeonPurple)))
                .align(Alignment.TopStart)
        )
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "// ANALISI VALORE COLLEZIONE",
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                color = TextSecondary,
                letterSpacing = 1.sp
            )
            Text(
                "BASATO SU MEDIA DI MERCATO EBAY",
                fontFamily = FontFamily.Monospace,
                fontSize = 8.sp,
                color = NeonGold.copy(0.5f),
                letterSpacing = 1.sp
            )
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
                    HudStat(
                        label = "MEDIA",
                        value = "€${"%.2f".format(collection.totalValue / collection.figureCount)}",
                        color = NeonPurple
                    )
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
private fun FigureCard(figure: ActionFigure, priceAlert: PriceAlert?, onDelete: () -> Unit) {
    val borderColor = when {
        priceAlert == null -> GridLine
        priceAlert.isUp -> NeonGreen.copy(0.4f)
        else -> NeonPink.copy(0.4f)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(DarkPanel2)
            .border(1.dp, borderColor, MaterialTheme.shapes.medium)
    ) {
        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(68.dp).clip(MaterialTheme.shapes.small)
                    .background(SpaceBlack).border(1.dp, GridLine, MaterialTheme.shapes.small),
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
                    color = MaterialTheme.colorScheme.onSurface
                )
                figure.condition?.let {
                    Spacer(Modifier.height(2.dp))
                    Text(it.uppercase(), fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = NeonCyan.copy(0.6f), letterSpacing = 1.sp)
                }
                Spacer(Modifier.height(4.dp))
                // Mostra prezzo medio di mercato (preferito) o prezzo listing
                val displayPrice = figure.averageMarketPrice ?: figure.price
                if (displayPrice != null) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            "€ ${"%.2f".format(displayPrice)}",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = NeonGold
                        )
                        if (figure.averageMarketPrice != null) {
                            Text(
                                "~MKT",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 8.sp,
                                color = NeonGold.copy(0.5f),
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
                // Indicatore trend se c'è un alert per questa figura
                if (priceAlert != null) {
                    Spacer(Modifier.height(2.dp))
                    val trendColor = if (priceAlert.isUp) NeonGreen else NeonPink
                    val sign = if (priceAlert.isUp) "+" else ""
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(
                            if (priceAlert.isUp) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                            contentDescription = null,
                            tint = trendColor,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            "$sign${"%.1f".format(priceAlert.changePercent)}% vs precedente",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 8.sp,
                            color = trendColor
                        )
                    }
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Rimuovi", tint = NeonPink, modifier = Modifier.size(20.dp))
            }
        }
    }
}