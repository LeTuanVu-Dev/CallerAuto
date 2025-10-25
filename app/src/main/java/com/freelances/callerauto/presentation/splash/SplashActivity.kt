package com.freelances.callerauto.presentation.splash

import android.os.Build
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.freelances.callerauto.databinding.ActivitySplashBinding
import com.freelances.callerauto.presentation.bases.BaseActivity
import com.freelances.callerauto.presentation.dialog.NoWifiPermissionDialog
import com.freelances.callerauto.presentation.language.LanguageActivity
import com.freelances.callerauto.presentation.login.LoginActivity
import com.freelances.callerauto.presentation.permission.PermissionActivity
import com.freelances.callerauto.utils.ext.isDefaultDialer
import com.freelances.callerauto.utils.ext.isGrantStoragePermission
import com.freelances.callerauto.utils.ext.openWifiSettings
import com.freelances.callerauto.utils.helper.LanguageHelper.preUpdateListLanguage
import com.freelances.callerauto.utils.helper.NetworkMonitor
import com.freelances.callerauto.utils.helper.OverlayPermissionHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {
    private val networkMonitor: NetworkMonitor by inject()

    override fun initViews() {
        listenersWifi()
    }

    private fun initData() {
        lifecycleScope.launch {
            preUpdateListLanguage()
            checkDoneFirstOpen()
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

    private fun listenersWifi() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                networkMonitor.isNetWorkReady.collectLatest {
                    if (it == false) {
                        showDialogNoWifi()
                    } else if (it == true) {
                        hideDialogNoWifi()
                        initData()
                    }
                }
            }
        }
    }

    private val noWifiPermissionDialog by lazy {
        NoWifiPermissionDialog().apply {
            setOnOkClick {
                openWifiSettings()
            }
        }
    }

    private fun showDialogNoWifi() {
        lifecycleScope.launch(Dispatchers.Main) {
            if (!noWifiPermissionDialog.isAdded) {
                noWifiPermissionDialog.show(supportFragmentManager, "Activity")
            }
        }
    }

    private fun hideDialogNoWifi() {
        lifecycleScope.launch(Dispatchers.Main) {
            if (noWifiPermissionDialog.isAdded)
                noWifiPermissionDialog.dismissAllowingStateLoss()
        }
    }


    override fun onStart() {
        super.onStart()
        networkMonitor.checkNetworkStatus()
        networkMonitor.startListening()
    }

    override fun onStop() {
        networkMonitor.stopListening()
        super.onStop()
    }
}