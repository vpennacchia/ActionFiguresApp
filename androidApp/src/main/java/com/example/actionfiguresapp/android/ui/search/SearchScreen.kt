package com.example.actionfiguresapp.android.ui.search

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
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
import com.example.actionfiguresapp.android.NeonGreen
import com.example.actionfiguresapp.android.SpaceBlack
import com.example.actionfiguresapp.android.TextSecondary
import com.example.actionfiguresapp.domain.model.ActionFigure
import com.example.actionfiguresapp.presentation.viewmodel.CollectionsViewModel
import com.example.actionfiguresapp.presentation.viewmodel.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    collectionId: String,
    searchViewModel: SearchViewModel,
    collectionsViewModel: CollectionsViewModel,
    onBack: () -> Unit
) {
    val searchState by searchViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(searchState.error) {
        searchState.error?.let { snackbarHostState.showSnackbar(it); searchViewModel.clearError() }
    }

    Scaffold(
        containerColor = SpaceBlack,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("SEARCH.EXE", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NeonGreen, letterSpacing = 2.sp)
                        Text("AGGIUNGI A COLLEZIONE", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = TextSecondary, letterSpacing = 1.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { searchViewModel.clearSearch(); onBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = NeonCyan)
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
                placeholder = { Text("Cerca su eBay...", fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = TextSecondary) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NeonGreen) },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = DarkPanel2,
                    unfocusedContainerColor = DarkPanel2,
                    focusedIndicatorColor = NeonGreen,
                    unfocusedIndicatorColor = GridLine,
                    focusedTextColor = NeonGreen,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    cursorColor = NeonGreen
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    searchState.isLoading -> CircularProgressIndicator(color = NeonGreen, modifier = Modifier.align(Alignment.Center))
                    searchState.query.length < 2 -> IdleState(modifier = Modifier.align(Alignment.Center))
                    searchState.results.isEmpty() && !searchState.isLoading -> Box(Modifier.align(Alignment.Center)) {
                        Text("[ NESSUN RISULTATO ]", fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = NeonCyan, letterSpacing = 2.sp)
                    }
                    else -> LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(searchState.results) { figure ->
                            SearchResultCard(figure = figure, onAdd = {
                                collectionsViewModel.addFigure(collectionId, figure)
                                searchViewModel.clearSearch()
                                onBack()
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IdleState(modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("[ EBAY SCANNER READY ]", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NeonGreen, letterSpacing = 2.sp)
        Spacer(Modifier.height(6.dp))
        Text("Min. 2 caratteri per avviare la ricerca", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TextSecondary, letterSpacing = 1.sp)
    }
}

@Composable
private fun SearchResultCard(figure: ActionFigure, onAdd: () -> Unit) {
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
            IconButton(onClick = onAdd) {
                Icon(Icons.Default.AddCircle, contentDescription = "Aggiungi", tint = NeonGreen, modifier = Modifier.size(32.dp))
            }
        }
    }
}
