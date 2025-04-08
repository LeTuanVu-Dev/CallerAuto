package com.freelances.callerauto.di

import com.freelances.callerauto.presentation.splash.SplashActivity
import com.freelances.callerauto.remoteconfig.RemoteConfig
import org.koin.dsl.module


val repoModule = module {
    //khai bao repo ton tai theo vong doi cua tung thanh phan
    scope<SplashActivity> {
        scoped {
            RemoteConfig(get())
        }
    }
}