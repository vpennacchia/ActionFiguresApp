package com.example.actionfiguresapp.android.ui.explore

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.actionfiguresapp.android.NeonPurple
import com.example.actionfiguresapp.android.SpaceBlack
import com.example.actionfiguresapp.android.TextSecondary
import com.example.actionfiguresapp.domain.model.ActionFigure
import com.example.actionfiguresapp.presentation.viewmodel.SearchViewModel
import com.example.actionfiguresapp.presentation.viewmodel.WishlistViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    searchViewModel: SearchViewModel,
    wishlistViewModel: WishlistViewModel
) {
    val searchState by searchViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(searchState.error) {
        searchState.error?.let { snackbarHostState.showSnackbar(it); searchViewModel.clearError() }
    }

    Scaffold(
        containerColor = SpaceBlack,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("EXPLORE.EXE", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NeonCyan, letterSpacing = 2.sp)
                        Text("RICERCA EBAY // MERCATO SECONDARIO", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = TextSecondary, letterSpacing = 1.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SpaceBlack)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TextField(
                value = searchState.query,
                onValueChange = { searchViewModel.onQueryChange(it) },
                placeholder = { Text("Cerca action figure...", fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = TextSecondary) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NeonCyan) },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = DarkPanel2,
                    unfocusedContainerColor = DarkPanel2,
                    focusedIndicatorColor = NeonCyan,
                    unfocusedIndicatorColor = GridLine,
                    focusedTextColor = NeonCyan,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    cursorColor = NeonCyan
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    searchState.isLoading -> CircularProgressIndicator(color = NeonCyan, modifier = Modifier.align(Alignment.Center))
                    searchState.query.length < 2 -> Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("[ SCANNER EBAY IN ATTESA ]", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NeonCyan, letterSpacing = 2.sp)
                        Spacer(Modifier.height(6.dp))
                        Text("Scrivi almeno 2 caratteri", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TextSecondary)
                    }
                    searchState.results.isEmpty() && !searchState.isLoading -> Box(Modifier.align(Alignment.Center)) {
                        Text("[ NESSUN RISULTATO ]", fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = NeonCyan, letterSpacing = 2.sp)
                    }
                    else -> LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(searchState.results) { figure ->
                            ExploreCard(
                                figure = figure,
                                onOpenEbay = {
                                    figure.ebayUrl?.let { url -> context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
                                },
                                onAddToWishlist = {
                                    wishlistViewModel.addToWishlist(figure)
                                    scope.launch { snackbarHostState.showSnackbar("Aggiunto alla wishlist") }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExploreCard(figure: ActionFigure, onOpenEbay: () -> Unit, onAddToWishlist: () -> Unit) {
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
                    Text(it.uppercase(), fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = NeonCyan.copy(0.6f), letterSpacing = 1.sp)
                }
                figure.price?.let {
                    Text("€ ${"%.2f".format(it)}", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NeonGold)
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = onOpenEbay) {
                    Icon(Icons.Default.OpenInBrowser, contentDescription = "Apri su eBay", tint = NeonCyan, modifier = Modifier.size(24.dp))
                }
                IconButton(onClick = onAddToWishlist) {
                    Icon(Icons.Default.Bookmark, contentDescription = "Wishlist", tint = NeonPurple, modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}
