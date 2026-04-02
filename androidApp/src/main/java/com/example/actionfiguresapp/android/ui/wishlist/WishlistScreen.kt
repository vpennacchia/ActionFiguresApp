package com.example.actionfiguresapp.android.ui.wishlist

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.actionfiguresapp.android.Gold
import com.example.actionfiguresapp.android.Purple
import com.example.actionfiguresapp.android.Teal
import com.example.actionfiguresapp.domain.model.ActionFigure
import com.example.actionfiguresapp.presentation.viewmodel.AuthViewModel
import com.example.actionfiguresapp.presentation.viewmodel.WishlistViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    authViewModel: AuthViewModel,
    wishlistViewModel: WishlistViewModel
) {
    val authState by authViewModel.uiState.collectAsState()
    val wishlistState by wishlistViewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.user) {
        authState.user?.uid?.let { wishlistViewModel.loadWishlist(it) }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Wishlist", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                wishlistState.isLoading -> {
                    CircularProgressIndicator(color = Purple, modifier = Modifier.align(Alignment.Center))
                }
                wishlistState.items.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.BookmarkBorder, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(64.dp))
                        Spacer(Modifier.height(16.dp))
                        Text("Wishlist vuota", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(4.dp))
                        Text("Cerca su eBay e aggiungi le figure che vuoi", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        WishlistSummaryBanner(
                            count = wishlistState.items.size,
                            totalValue = wishlistState.items.sumOf { it.price ?: 0.0 }
                        )
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(wishlistState.items, key = { it.id }) { figure ->
                                WishlistItemCard(
                                    figure = figure,
                                    onOpenEbay = {
                                        figure.ebayUrl?.let { url ->
                                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                                        }
                                    },
                                    onRemove = { wishlistViewModel.removeFromWishlist(figure.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WishlistSummaryBanner(count: Int, totalValue: Double) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(MaterialTheme.shapes.large)
            .background(Brush.linearGradient(listOf(Color(0xFF1A1040), Color(0xFF0A2030))))
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("$count figure desiderate", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("€ %.2f".format(totalValue), style = MaterialTheme.typography.headlineSmall, color = Gold, fontWeight = FontWeight.Bold)
            }
            Icon(Icons.Default.BookmarkBorder, contentDescription = null, tint = Teal, modifier = Modifier.size(36.dp))
        }
    }
}

@Composable
private fun WishlistItemCard(figure: ActionFigure, onOpenEbay: () -> Unit, onRemove: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = MaterialTheme.shapes.large
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(72.dp).clip(MaterialTheme.shapes.medium).background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                if (figure.imageUrl != null) {
                    AsyncImage(model = figure.imageUrl, contentDescription = figure.name, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                } else {
                    Icon(Icons.Default.SmartToy, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(figure.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, maxLines = 2)
                figure.condition?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                figure.price?.let {
                    Text("€ %.2f".format(it), style = MaterialTheme.typography.labelLarge, color = Gold, fontWeight = FontWeight.Bold)
                }
            }

            figure.ebayUrl?.let {
                IconButton(onClick = onOpenEbay) {
                    Icon(Icons.Default.OpenInBrowser, contentDescription = "Apri su eBay", tint = Purple, modifier = Modifier.size(24.dp))
                }
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Rimuovi", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(24.dp))
            }
        }
    }
}
