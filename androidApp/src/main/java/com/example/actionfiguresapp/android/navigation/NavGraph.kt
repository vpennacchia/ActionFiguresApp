package com.example.actionfiguresapp.android.navigation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.actionfiguresapp.android.DarkPanel
import com.example.actionfiguresapp.android.GridLine
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.actionfiguresapp.android.NeonCyan
import com.example.actionfiguresapp.android.ui.auth.LoginScreen
import com.example.actionfiguresapp.android.ui.auth.RegisterScreen
import com.example.actionfiguresapp.android.ui.collections.CollectionDetailScreen
import com.example.actionfiguresapp.android.ui.collections.CollectionsScreen
import com.example.actionfiguresapp.android.ui.explore.ExploreScreen
import com.example.actionfiguresapp.android.ui.search.SearchScreen
import com.example.actionfiguresapp.android.ui.wishlist.WishlistScreen
import com.example.actionfiguresapp.presentation.viewmodel.AuthViewModel
import com.example.actionfiguresapp.presentation.viewmodel.CollectionsViewModel
import com.example.actionfiguresapp.presentation.viewmodel.SearchViewModel
import com.example.actionfiguresapp.presentation.viewmodel.WishlistViewModel
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

private const val ROUTE_LOGIN = "login"
private const val ROUTE_REGISTER = "register"
private const val ROUTE_MAIN = "main"
private const val ROUTE_COLLECTION_DETAIL = "collection_detail/{collectionId}"
private const val ROUTE_SEARCH_FOR_COLLECTION = "search_for_collection/{collectionId}"

private val bottomNavRoutes = BottomNavItem.items.map { it.route }.toSet()

@Composable
fun NavGraph(navController: NavHostController) {
    // Tutti i ViewModel creati una sola volta → stesso stato condiviso in tutta la navigazione
    val authViewModel: AuthViewModel = koinInject()
    val collectionsViewModel: CollectionsViewModel = koinInject()
    val wishlistViewModel: WishlistViewModel = koinInject()
    val exploreSearchViewModel: SearchViewModel = koinInject(qualifier = named("explore"))
    val collectionSearchViewModel: SearchViewModel = koinInject(qualifier = named("collection"))

    val authState by authViewModel.uiState.collectAsState()

    val startDestination = if (authState.user != null) ROUTE_MAIN else ROUTE_LOGIN

    NavHost(navController = navController, startDestination = startDestination) {
        composable(ROUTE_LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(ROUTE_MAIN) {
                        popUpTo(ROUTE_LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(ROUTE_REGISTER) }
            )
        }

        composable(ROUTE_REGISTER) {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate(ROUTE_MAIN) {
                        popUpTo(ROUTE_LOGIN) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable(ROUTE_MAIN) {
            MainWithBottomNav(
                authViewModel = authViewModel,
                collectionsViewModel = collectionsViewModel,
                wishlistViewModel = wishlistViewModel,
                exploreSearchViewModel = exploreSearchViewModel,
                onCollectionClick = { collectionId ->
                    navController.navigate("collection_detail/$collectionId")
                },
                onSignOut = {
                    navController.navigate(ROUTE_LOGIN) {
                        popUpTo(ROUTE_MAIN) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = ROUTE_COLLECTION_DETAIL,
            arguments = listOf(navArgument("collectionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val collectionId = backStackEntry.arguments?.getString("collectionId") ?: return@composable
            CollectionDetailScreen(
                collectionId = collectionId,
                collectionsViewModel = collectionsViewModel,
                onAddFigure = { navController.navigate("search_for_collection/$collectionId") },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = ROUTE_SEARCH_FOR_COLLECTION,
            arguments = listOf(navArgument("collectionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val collectionId = backStackEntry.arguments?.getString("collectionId") ?: return@composable
            SearchScreen(
                collectionId = collectionId,
                searchViewModel = collectionSearchViewModel,
                collectionsViewModel = collectionsViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
private fun MainWithBottomNav(
    authViewModel: AuthViewModel,
    collectionsViewModel: CollectionsViewModel,
    wishlistViewModel: WishlistViewModel,
    exploreSearchViewModel: SearchViewModel,
    onCollectionClick: (String) -> Unit,
    onSignOut: () -> Unit
) {
    val bottomNavController = rememberNavController()
    val currentBackStack by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            val barShape = RoundedCornerShape(24.dp)
            NavigationBar(
                containerColor = DarkPanel,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .fillMaxWidth()
                    .clip(barShape)
                    .border(1.dp, GridLine, barShape)
            ) {
                BottomNavItem.items.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            bottomNavController.navigate(item.route) {
                                popUpTo(bottomNavController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label, style = MaterialTheme.typography.labelSmall) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonCyan,
                            selectedTextColor = NeonCyan,
                            indicatorColor = NeonCyan.copy(alpha = 0.12f),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = BottomNavItem.Collections.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Collections.route) {
                CollectionsScreen(
                    authViewModel = authViewModel,
                    collectionsViewModel = collectionsViewModel,
                    onCollectionClick = onCollectionClick,
                    onSignOut = onSignOut
                )
            }
            composable(BottomNavItem.Explore.route) {
                ExploreScreen(
                    searchViewModel = exploreSearchViewModel,
                    wishlistViewModel = wishlistViewModel
                )
            }
            composable(BottomNavItem.Wishlist.route) {
                WishlistScreen(
                    authViewModel = authViewModel,
                    wishlistViewModel = wishlistViewModel
                )
            }
        }
    }
}
