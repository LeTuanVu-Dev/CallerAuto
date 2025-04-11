package com.freelances.callerauto.presentation.splash

import androidx.lifecycle.lifecycleScope
import com.freelances.callerauto.databinding.ActivitySplashBinding
import com.freelances.callerauto.presentation.bases.BaseActivity
import com.freelances.callerauto.presentation.language.LanguageActivity
import com.freelances.callerauto.presentation.login.LoginActivity
import com.freelances.callerauto.remoteconfig.RemoteConfig
import com.freelances.callerauto.utils.helper.DeviceKeyManager
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

    override fun initViews() {
        lifecycleScope.launch {
            preUpdateListLanguage()
            checkDoneFirstOpen()
            checkDataKey()
            moveScreen()
        }
    }

    private fun checkDataKey() {
        DeviceKeyManager.getOrValidateDeviceKey(this, "") { _, _ ->
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