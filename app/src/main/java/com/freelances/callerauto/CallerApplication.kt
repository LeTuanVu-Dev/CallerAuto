package com.freelances.callerauto

import android.app.Application
import com.freelances.callerauto.di.appModule
import com.freelances.callerauto.di.repoModule
import com.freelances.callerauto.di.viewModelModule
import com.freelances.callerauto.utils.ext.setApplicationContext
import com.google.firebase.FirebaseApp
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class CallerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        setApplicationContext(this)
        startKoin {
            androidLogger()
            androidContext(this@CallerApplication)
            modules(
                repoModule, appModule, viewModelModule
            )
        }
    }
}