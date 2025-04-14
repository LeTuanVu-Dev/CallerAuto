package com.freelances.callerauto.presentation.splash

import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.freelances.callerauto.databinding.ActivitySplashBinding
import com.freelances.callerauto.presentation.bases.BaseActivity
import com.freelances.callerauto.presentation.language.LanguageActivity
import com.freelances.callerauto.presentation.login.LoginActivity
import com.freelances.callerauto.utils.ext.isDualSimActive
import com.freelances.callerauto.utils.helper.DeviceKeyManager
import com.freelances.callerauto.utils.helper.LanguageHelper.preUpdateListLanguage
import kotlinx.coroutines.launch

class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {
    override fun initViews() {
        lifecycleScope.launch {
            sharedPreference.currentSimType =
                if (isDualSimActive(this@SplashActivity)) sharedPreference.currentSimType else -1
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