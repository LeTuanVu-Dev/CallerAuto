package com.freelances.callerauto.presentation.splash

import android.os.Build
import androidx.lifecycle.lifecycleScope
import com.freelances.callerauto.databinding.ActivitySplashBinding
import com.freelances.callerauto.presentation.bases.BaseActivity
import com.freelances.callerauto.presentation.language.LanguageActivity
import com.freelances.callerauto.presentation.login.LoginActivity
import com.freelances.callerauto.presentation.permission.PermissionActivity
import com.freelances.callerauto.utils.ext.isDefaultDialer
import com.freelances.callerauto.utils.ext.isGrantStoragePermission
import com.freelances.callerauto.utils.helper.DeviceKeyManager
import com.freelances.callerauto.utils.helper.LanguageHelper.preUpdateListLanguage
import com.freelances.callerauto.utils.helper.OverlayPermissionHelper
import kotlinx.coroutines.launch

class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {
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
        val isCheckPermission =
            OverlayPermissionHelper.hasOverlayPermission(this) && isDefaultDialer(this) && (isGrantStoragePermission() || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU))
        if (!isCheckPermission) {
            navigateTo(
                PermissionActivity::class.java,
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