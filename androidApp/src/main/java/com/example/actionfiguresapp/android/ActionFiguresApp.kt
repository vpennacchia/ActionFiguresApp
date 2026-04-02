package com.example.actionfiguresapp.android

import android.app.Application
import com.example.actionfiguresapp.android.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ActionFiguresApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ActionFiguresApp)
            modules(appModule)
        }
    }
}
