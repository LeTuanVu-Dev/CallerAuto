package com.freelances.callerauto.presentation.splash

import android.provider.Settings
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.freelances.callerauto.databinding.ActivitySplashBinding
import com.freelances.callerauto.presentation.bases.BaseActivity
import com.freelances.callerauto.presentation.language.LanguageActivity
import com.freelances.callerauto.presentation.login.LoginActivity
import com.freelances.callerauto.remoteconfig.RemoteConfig
import com.freelances.callerauto.utils.helper.LanguageHelper.preUpdateListLanguage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import org.koin.core.component.inject

class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {
    companion object {
        private const val TIME_OUT = 30_000L
        private const val TIME_DELAY = 6_000L
    }

    private val remoteConfig: RemoteConfig by inject()

    override fun initViews() {
        lifecycleScope.launch {
            val job1 = launch {
                withTimeoutOrNull(TIME_OUT) {
                    remoteConfig.fetchRemoteData()
                }
            }
            job1.join()
            preUpdateListLanguage()
            checkDoneFirstOpen()
            delay(TIME_DELAY)
            moveScreen()
        }
    }

    private fun checkDoneFirstOpen() {
        if (!sharedPreference.isDoneFirstOpen) {
            sharedPreference.languageCode = ""
        }
    }

    override fun isDisplayCutout(): Boolean = true

    private fun moveScreen() {
        if (!sharedPreference.isDoneFirstOpen) {
            navigateTo(
                LanguageActivity::class.java,
                navigationAnimation = null,
                isFinish = true
            )
            return
        }
        navigateTo(
            LoginActivity::class.java,
            navigationAnimation = null,
            isFinish = true
        )

    }
}