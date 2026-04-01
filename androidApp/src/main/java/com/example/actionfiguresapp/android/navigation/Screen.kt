package com.example.actionfiguresapp.android.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Collections : Screen("collections")
    object CollectionDetail : Screen("collection_detail/{collectionId}") {
        fun createRoute(collectionId: String) = "collection_detail/$collectionId"
    }
    object Search : Screen("search/{collectionId}") {
        fun createRoute(collectionId: String) = "search/$collectionId"
    }
}
