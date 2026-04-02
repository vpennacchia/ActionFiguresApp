package com.example.actionfiguresapp.android.di

import com.example.actionfiguresapp.android.BuildConfig
import com.example.actionfiguresapp.data.remote.ebay.EbayApiService
import com.example.actionfiguresapp.data.repository.AuthRepositoryImpl
import com.example.actionfiguresapp.data.repository.CollectionRepositoryImpl
import com.example.actionfiguresapp.data.repository.SearchRepositoryImpl
import com.example.actionfiguresapp.data.repository.WishlistRepositoryImpl
import com.example.actionfiguresapp.domain.repository.AuthRepository
import com.example.actionfiguresapp.domain.repository.CollectionRepository
import com.example.actionfiguresapp.domain.repository.SearchRepository
import com.example.actionfiguresapp.domain.repository.WishlistRepository
import com.example.actionfiguresapp.presentation.viewmodel.AuthViewModel
import com.example.actionfiguresapp.presentation.viewmodel.CollectionsViewModel
import com.example.actionfiguresapp.presentation.viewmodel.SearchViewModel
import com.example.actionfiguresapp.presentation.viewmodel.WishlistViewModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {

    // Ktor HTTP Client
    single {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            install(Logging) { level = LogLevel.BODY }
        }
    }

    // Firebase
    single { Firebase.auth }
    single { Firebase.firestore }

    // eBay API Service
    single {
        EbayApiService(
            httpClient = get(),
            clientId = BuildConfig.EBAY_CLIENT_ID,
            clientSecret = BuildConfig.EBAY_CLIENT_SECRET
        )
    }

    // Repository
    single<AuthRepository> { AuthRepositoryImpl(firebaseAuth = get()) }
    single<CollectionRepository> { CollectionRepositoryImpl(firestore = get()) }
    single<SearchRepository> { SearchRepositoryImpl(ebayApiService = get()) }
    single<WishlistRepository> { WishlistRepositoryImpl(firestore = get()) }

    // ViewModel
    viewModel { AuthViewModel(authRepository = get()) }
    viewModel { CollectionsViewModel(collectionRepository = get()) }
    viewModel { WishlistViewModel(wishlistRepository = get()) }

    // Due istanze separate di SearchViewModel: una per Esplora, una per aggiungere a collezione
    viewModel(qualifier = named("explore")) { SearchViewModel(searchRepository = get()) }
    viewModel(qualifier = named("collection")) { SearchViewModel(searchRepository = get()) }
}
