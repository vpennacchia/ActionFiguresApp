package com.example.actionfiguresapp.android.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object Collections : BottomNavItem("collections", "Collezioni", Icons.Default.SmartToy)
    object Explore : BottomNavItem("explore", "Esplora", Icons.Default.Search)
    object Wishlist : BottomNavItem("wishlist", "Wishlist", Icons.Default.BookmarkBorder)

    companion object {
        val items = listOf(Collections, Explore, Wishlist)
    }
}
