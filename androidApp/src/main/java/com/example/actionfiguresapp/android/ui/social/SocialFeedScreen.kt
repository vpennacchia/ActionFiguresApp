package com.example.actionfiguresapp.android.ui.social

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PersonSearch
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
import com.example.actionfiguresapp.android.NeonPink
import com.example.actionfiguresapp.android.NeonPurple
import com.example.actionfiguresapp.android.SpaceBlack
import com.example.actionfiguresapp.android.TextSecondary
import com.example.actionfiguresapp.domain.model.Post
import com.example.actionfiguresapp.presentation.viewmodel.AuthViewModel
import com.example.actionfiguresapp.presentation.viewmodel.SocialFeedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialFeedScreen(
    authViewModel: AuthViewModel,
    socialFeedViewModel: SocialFeedViewModel,
    onCreatePost: () -> Unit,
    onSearchUsers: () -> Unit
) {
    val authState by authViewModel.uiState.collectAsState()
    val feedState by socialFeedViewModel.uiState.collectAsState()
    val currentUserId = authState.user?.uid ?: ""

    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            socialFeedViewModel.loadFeed(currentUserId)
        }
    }

    Scaffold(
        containerColor = SpaceBlack,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "SOCIAL.FEED",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = NeonCyan
                    )
                },
                actions = {
                    IconButton(onClick = onSearchUsers) {
                        Icon(Icons.Default.PersonSearch, contentDescription = "Cerca utenti", tint = NeonPurple)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SpaceBlack)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreatePost,
                containerColor = NeonCyan,
                contentColor = SpaceBlack,
                shape = MaterialTheme.shapes.small
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Nuovo post")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                feedState.isLoading && feedState.posts.isEmpty() ->
                    CircularProgressIndicator(color = NeonCyan, modifier = Modifier.align(Alignment.Center))
                feedState.posts.isEmpty() ->
                    Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("[ FEED VUOTO ]", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NeonCyan, letterSpacing = 2.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("Sii il primo a postare!", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = TextSecondary)
                    }
                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(feedState.posts) { post ->
                        PostCard(
                            post = post,
                            onLikeClick = { socialFeedViewModel.toggleLike(post.id, currentUserId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PostCard(post: Post, onLikeClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkPanel2, MaterialTheme.shapes.medium)
            .border(1.dp, GridLine, MaterialTheme.shapes.medium)
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(NeonCyan.copy(alpha = 0.3f)))
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(SpaceBlack)
                        .border(1.dp, NeonPurple.copy(0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = post.authorName.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = NeonPurple
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(post.authorName, style = MaterialTheme.typography.labelLarge, color = Color.White)
                    Text(
                        formatTimestamp(post.createdAt),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        color = TextSecondary
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(post.text, style = MaterialTheme.typography.bodyMedium, color = Color.White)
            if (post.imageUrl != null) {
                Spacer(Modifier.height(10.dp))
                AsyncImage(
                    model = post.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(MaterialTheme.shapes.small)
                        .border(1.dp, GridLine, MaterialTheme.shapes.small)
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onLikeClick, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = if (post.isLikedByCurrentUser) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (post.isLikedByCurrentUser) NeonPink else TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "${post.likeCount}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = if (post.isLikedByCurrentUser) NeonPink else TextSecondary
                )
            }
        }
    }
}

private fun formatTimestamp(epochSeconds: Long): String {
    val now = System.currentTimeMillis() / 1000L
    val diff = now - epochSeconds
    return when {
        diff < 60 -> "ora"
        diff < 3600 -> "${diff / 60}m fa"
        diff < 86400 -> "${diff / 3600}h fa"
        else -> "${diff / 86400}g fa"
    }
}
