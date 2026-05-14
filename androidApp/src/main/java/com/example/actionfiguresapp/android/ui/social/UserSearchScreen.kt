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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.actionfiguresapp.android.DarkPanel2
import com.example.actionfiguresapp.android.GridLine
import com.example.actionfiguresapp.android.NeonCyan
import com.example.actionfiguresapp.android.NeonPurple
import com.example.actionfiguresapp.android.SpaceBlack
import com.example.actionfiguresapp.android.TextPrimary
import com.example.actionfiguresapp.android.TextSecondary
import com.example.actionfiguresapp.domain.model.PublicUserProfile
import com.example.actionfiguresapp.presentation.viewmodel.UserSearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSearchScreen(
    userSearchViewModel: UserSearchViewModel,
    onUserClick: (String) -> Unit,
    onBack: () -> Unit
) {
    val searchState by userSearchViewModel.uiState.collectAsState()

    Scaffold(
        containerColor = SpaceBlack,
        topBar = {
            TopAppBar(
                title = {
                    Text("CERCA_UTENTI", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, letterSpacing = 2.sp, color = NeonCyan)
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
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkPanel2, MaterialTheme.shapes.medium)
                    .border(1.dp, GridLine, MaterialTheme.shapes.medium)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    BasicTextField(
                        value = searchState.query,
                        onValueChange = { userSearchViewModel.onQueryChange(it) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        cursorBrush = SolidColor(NeonCyan),
                        textStyle = TextStyle(
                            color = TextPrimary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp
                        ),
                        decorationBox = { inner ->
                            if (searchState.query.isEmpty()) {
                                Text("Cerca per nome...", fontFamily = FontFamily.Monospace, fontSize = 14.sp, color = TextSecondary)
                            }
                            inner()
                        }
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            when {
                searchState.isLoading ->
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = NeonCyan, modifier = Modifier.size(28.dp))
                    }
                searchState.query.isNotBlank() && searchState.results.isEmpty() && !searchState.isLoading ->
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 32.dp), contentAlignment = Alignment.Center) {
                        Text("[ NESSUN UTENTE TROVATO ]", fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = TextSecondary, letterSpacing = 1.sp)
                    }
                else -> LazyColumn(
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(searchState.results) { user ->
                        UserResultItem(user = user, onClick = { onUserClick(user.uid) })
                    }
                }
            }
        }
    }
}

@Composable
private fun UserResultItem(user: PublicUserProfile, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkPanel2, MaterialTheme.shapes.medium)
            .border(1.dp, GridLine, MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(SpaceBlack)
                .border(1.dp, NeonPurple.copy(0.6f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = NeonPurple
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(user.displayName, style = MaterialTheme.typography.titleMedium, color = Color.White)
            if (user.bio.isNotBlank()) {
                Text(user.bio, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 1)
            }
            Text(
                "${user.collectionsCount} collezioni",
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                color = NeonCyan.copy(0.7f)
            )
        }
    }
}
