package com.example.actionfiguresapp.android.ui.wishlist

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.OpenInBrowser
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
import com.example.actionfiguresapp.android.NeonGold
import com.example.actionfiguresapp.android.NeonPink
import com.example.actionfiguresapp.android.NeonPurple
import com.example.actionfiguresapp.android.SpaceBlack
import com.example.actionfiguresapp.android.TextSecondary
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
        containerColor = SpaceBlack,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("WISHLIST.DB", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NeonPurple, letterSpacing = 2.sp)
                        Text("FIGURE DESIDERATE // TARGET LIST", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = TextSecondary, letterSpacing = 1.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SpaceBlack)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                wishlistState.isLoading -> CircularProgressIndicator(color = NeonPurple, modifier = Modifier.align(Alignment.Center))
                wishlistState.items.isEmpty() -> Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("[ WISHLIST VUOTA ]", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NeonPurple, letterSpacing = 2.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Cerca su Esplora e aggiungi con l'icona bookmark", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TextSecondary, letterSpacing = 1.sp)
                }
                else -> Column(modifier = Modifier.fillMaxSize()) {
                    WishlistHudBanner(count = wishlistState.items.size, totalValue = wishlistState.items.sumOf { it.price ?: 0.0 })
                    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(wishlistState.items, key = { it.id }) { figure ->
                            WishlistItemCard(
                                figure = figure,
                                onOpenEbay = { figure.ebayUrl?.let { url -> context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) } },
                                onRemove = { wishlistViewModel.removeFromWishlist(figure.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WishlistHudBanner(count: Int, totalValue: Double) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(Brush.linearGradient(listOf(Color(0xFF1A0030), Color(0xFF0D0026))))
            .border(1.dp, NeonPurple.copy(0.4f), MaterialTheme.shapes.medium)
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(Brush.horizontalGradient(listOf(NeonPurple, NeonCyan))).align(Alignment.TopStart))
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("// BUDGET TARGET", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = TextSecondary, letterSpacing = 1.sp)
                Spacer(Modifier.height(4.dp))
                Text("€ ${"%,.2f".format(totalValue)}", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = NeonGold, letterSpacing = 1.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("ITEMS", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = TextSecondary, letterSpacing = 1.sp)
                Text("$count", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = NeonPurple)
            }
        }
    }
}

@Composable
private fun WishlistItemCard(figure: ActionFigure, onOpenEbay: () -> Unit, onRemove: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().clip(MaterialTheme.shapes.medium)
            .background(DarkPanel2).border(1.dp, GridLine, MaterialTheme.shapes.medium)
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
                Text(figure.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, maxLines = 2)
                figure.condition?.let {
                    Text(it.uppercase(), fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = NeonPurple.copy(0.6f), letterSpacing = 1.sp)
                }
                figure.price?.let {
                    Text("€ ${"%.2f".format(it)}", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NeonGold)
                }
            }
            figure.ebayUrl?.let {
                IconButton(onClick = onOpenEbay) {
                    Icon(Icons.Default.OpenInBrowser, contentDescription = "Apri su eBay", tint = NeonCyan, modifier = Modifier.size(22.dp))
                }
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Rimuovi", tint = NeonPink, modifier = Modifier.size(22.dp))
            }
        }
    }
}
