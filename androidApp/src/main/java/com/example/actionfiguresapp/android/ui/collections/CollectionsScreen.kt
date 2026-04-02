package com.example.actionfiguresapp.android.ui.collections

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.actionfiguresapp.android.DarkPanel2
import com.example.actionfiguresapp.android.GridLine
import com.example.actionfiguresapp.android.NeonCyan
import com.example.actionfiguresapp.android.NeonGold
import com.example.actionfiguresapp.android.NeonGreen
import com.example.actionfiguresapp.android.NeonPink
import com.example.actionfiguresapp.android.NeonPurple
import com.example.actionfiguresapp.android.SpaceBlack
import com.example.actionfiguresapp.android.TextSecondary
import com.example.actionfiguresapp.domain.model.Collection
import com.example.actionfiguresapp.presentation.viewmodel.AuthViewModel
import com.example.actionfiguresapp.presentation.viewmodel.CollectionsViewModel

private val cardGradients = listOf(
    listOf(Color(0xFF003D52), Color(0xFF001A2E)) to NeonCyan,
    listOf(Color(0xFF2D0052), Color(0xFF0D0026)) to NeonPurple,
    listOf(Color(0xFF003320), Color(0xFF001A0D)) to NeonGreen,
    listOf(Color(0xFF520028), Color(0xFF260014)) to NeonPink,
    listOf(Color(0xFF524200), Color(0xFF261F00)) to NeonGold,
    listOf(Color(0xFF001A52), Color(0xFF000D26)) to Color(0xFF4D9FFF),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionsScreen(
    authViewModel: AuthViewModel,
    collectionsViewModel: CollectionsViewModel,
    onCollectionClick: (String) -> Unit,
    onSignOut: () -> Unit
) {
    val authState by authViewModel.uiState.collectAsState()
    val collectionsState by collectionsViewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    LaunchedEffect(authState.user) {
        authState.user?.uid?.let { collectionsViewModel.loadCollections(it) }
    }

    Scaffold(
        containerColor = SpaceBlack,
        topBar = {
            TopAppBar(
                title = {
                    Text("MY_COLLECTIONS", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, letterSpacing = 2.sp, color = NeonCyan)
                },
                actions = {
                    authState.user?.displayName?.firstOrNull()?.let { initial ->
                        Box(
                            modifier = Modifier.size(30.dp).background(DarkPanel2, MaterialTheme.shapes.small).border(1.dp, NeonCyan.copy(0.5f), MaterialTheme.shapes.small),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(initial.uppercaseChar().toString(), fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NeonCyan)
                        }
                        Spacer(Modifier.width(8.dp))
                    }
                    IconButton(onClick = { authViewModel.signOut(); onSignOut() }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Esci", tint = TextSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SpaceBlack)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = NeonCyan,
                contentColor = SpaceBlack,
                shape = MaterialTheme.shapes.small
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuova collezione")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                collectionsState.isLoading -> CircularProgressIndicator(color = NeonCyan, modifier = Modifier.align(Alignment.Center))
                collectionsState.collections.isEmpty() -> EmptyState(modifier = Modifier.align(Alignment.Center))
                else -> LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(collectionsState.collections) { collection ->
                        val (gradient, neon) = cardGradients[collectionsState.collections.indexOf(collection) % cardGradients.size]
                        CollectionCard(collection = collection, gradient = gradient, neonColor = neon, onClick = { onCollectionClick(collection.id) })
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateCollectionDialog(
            onConfirm = { name, description ->
                authState.user?.uid?.let { collectionsViewModel.createCollection(it, name, description) }
                showCreateDialog = false
            },
            onDismiss = { showCreateDialog = false }
        )
    }
}

@Composable
private fun CollectionCard(collection: Collection, gradient: List<Color>, neonColor: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(Brush.linearGradient(gradient))
            .border(1.dp, neonColor.copy(alpha = 0.5f), MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
    ) {
        // Top neon strip
        Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(neonColor).align(Alignment.TopStart))

        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.SmartToy, contentDescription = null, tint = neonColor.copy(0.6f), modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("SYS://COL", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = neonColor.copy(0.5f), letterSpacing = 1.sp)
            }
            Column {
                Text(text = collection.name, style = MaterialTheme.typography.titleMedium, color = Color.White, maxLines = 2)
                Spacer(Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = "${collection.figureCount}x",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = neonColor.copy(0.7f)
                    )
                    Text(
                        text = "€${"%,.0f".format(collection.totalValue)}",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = NeonGold
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("[ NO DATA FOUND ]", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NeonCyan, letterSpacing = 2.sp)
        Spacer(Modifier.height(8.dp))
        Text("Premi + per creare la tua prima collezione", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = TextSecondary, letterSpacing = 1.sp)
    }
}

@Composable
private fun CreateCollectionDialog(onConfirm: (String, String) -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkPanel,
        shape = MaterialTheme.shapes.medium,
        title = {
            Text("NEW_COLLECTION.INIT()", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NeonCyan, letterSpacing = 1.sp)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("NOME *", fontFamily = FontFamily.Monospace, fontSize = 11.sp) },
                    singleLine = true,
                    shape = MaterialTheme.shapes.small,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonCyan, focusedLabelColor = NeonCyan, unfocusedBorderColor = GridLine, unfocusedLabelColor = TextSecondary),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("DESCRIZIONE", fontFamily = FontFamily.Monospace, fontSize = 11.sp) },
                    singleLine = true,
                    shape = MaterialTheme.shapes.small,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonPurple, focusedLabelColor = NeonPurple, unfocusedBorderColor = GridLine, unfocusedLabelColor = TextSecondary),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name, description) }, enabled = name.isNotBlank()) {
                Text("[ CREA ]", fontFamily = FontFamily.Monospace, color = NeonCyan, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("[ ANNULLA ]", fontFamily = FontFamily.Monospace, color = TextSecondary) }
        }
    )
}
