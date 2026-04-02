package com.example.actionfiguresapp.android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.actionfiguresapp.android.ui.auth.LoginScreen
import com.example.actionfiguresapp.android.ui.auth.RegisterScreen
import com.example.actionfiguresapp.android.ui.collections.CollectionDetailScreen
import com.example.actionfiguresapp.android.ui.collections.CollectionsScreen
import com.example.actionfiguresapp.android.ui.search.SearchScreen
import com.example.actionfiguresapp.presentation.viewmodel.AuthViewModel
import com.example.actionfiguresapp.presentation.viewmodel.CollectionsViewModel
import com.example.actionfiguresapp.presentation.viewmodel.SearchViewModel
import org.koin.compose.koinInject

@Composable
fun NavGraph(navController: NavHostController) {
    val authViewModel: AuthViewModel = koinInject()
    val collectionsViewModel: CollectionsViewModel = koinInject()
    val searchViewModel: SearchViewModel = koinInject()

    val authState by authViewModel.uiState.collectAsState()

    val startDestination = if (authState.user != null) {
        Screen.Collections.route
    } else {
        Screen.Login.route
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Collections.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate(Screen.Collections.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Collections.route) {
            CollectionsScreen(
                authViewModel = authViewModel,
                collectionsViewModel = collectionsViewModel,
                onCollectionClick = { collectionId ->
                    navController.navigate(Screen.CollectionDetail.createRoute(collectionId))
                },
                onSignOut = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Collections.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.CollectionDetail.route,
            arguments = listOf(navArgument("collectionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val collectionId = backStackEntry.arguments?.getString("collectionId") ?: return@composable
            CollectionDetailScreen(
                collectionId = collectionId,
                collectionsViewModel = collectionsViewModel,
                onAddFigure = {
                    navController.navigate(Screen.Search.createRoute(collectionId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Search.route,
            arguments = listOf(navArgument("collectionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val collectionId = backStackEntry.arguments?.getString("collectionId") ?: return@composable
            SearchScreen(
                collectionId = collectionId,
                searchViewModel = searchViewModel,
                collectionsViewModel = collectionsViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
