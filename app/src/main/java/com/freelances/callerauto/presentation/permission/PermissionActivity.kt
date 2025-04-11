package com.freelances.callerauto.presentation.permission

import android.Manifest
import android.app.Activity
import android.app.AppOpsManager
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Binder
import android.os.Build
import android.provider.Settings
import android.telecom.TelecomManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.freelances.callerauto.R
import com.freelances.callerauto.databinding.ActivityPermissionBinding
import com.freelances.callerauto.presentation.bases.BaseActivity
import com.freelances.callerauto.presentation.login.LoginActivity
import com.freelances.callerauto.utils.device.BackgroundStartPermission
import com.freelances.callerauto.utils.device.DeviceUtil
import com.freelances.callerauto.utils.device.PermissionsSettingsUtil.launchAppPermissionsSettings
import com.freelances.callerauto.utils.ext.getIntentSettingsPermission
import com.freelances.callerauto.utils.ext.isGrantStoragePermission
import com.freelances.callerauto.utils.ext.readPhonePermissionGrant
import com.freelances.callerauto.utils.ext.safeClick
import com.freelances.callerauto.utils.ext.visible


open class PermissionActivity :
    BaseActivity<ActivityPermissionBinding>(ActivityPermissionBinding::inflate) {
    private val resultLauncherStorage =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { _ ->
            checkToggle()
        }

    override fun onResume() {
        super.onResume()
        checkToggle()

    }


    override fun initViews() {
        updateButtonDoneState()
        checkToggle()
        permissionCallDefault()

        binding.ivToggleStorage.safeClick {
            val showRationale =
                shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) ||
                        shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)

            if (isGrantStoragePermission()) {
                checkToggle()
            } else if (!showRationale) {
                resultLauncherStorage.launch(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
            } else {
                openAppSettings()
            }
        }

        binding.ivToggleCallDefault.safeClick {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                launchSetDefaultDialerIntent(this)
            } else {
                if (!BackgroundStartPermission.isBackgroundStartAllowed(this)) {
                    grandPopUpPermissionLauncher.launchAppPermissionsSettings(packageName)
                } else {
                    checkToggle()
                }
            }
        }
    }

    open fun checkToggle() {
        binding.ivToggleCallDefault.isEnabled = !readPhonePermissionGrant(this)
        binding.ivToggleStorage.isEnabled =
            (!isGrantStoragePermission() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)

        binding.ivToggleCallDefault.setImageResource(if (readPhonePermissionGrant(this)) R.drawable.ic_toggle_on else R.drawable.ic_toggle_off)
        binding.ivToggleStorage.setImageResource(if (isGrantStoragePermission() || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)) R.drawable.ic_toggle_on else R.drawable.ic_toggle_off)

        if (readPhonePermissionGrant(this) && (isGrantStoragePermission() || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU))) {
            binding.tvContinue.visible()
        }
    }


    private fun openAppSettings() {
        startActivity(getIntentSettingsPermission())
    }

    private fun updateButtonDoneState() {
        binding.tvContinue.safeClick {
            navigateTo(LoginActivity::class.java, isFinish = true)
        }
    }


    private lateinit var setDefaultDialerLauncher: ActivityResultLauncher<Intent>
    private fun launchSetDefaultDialerIntent(activity: AppCompatActivity) {
        Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER).putExtra(
            TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME,
            activity.packageName
        ).apply {
            if (resolveActivity(activity.packageManager) != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val rm: RoleManager? = activity.getSystemService(RoleManager::class.java)
                    if (rm?.isRoleAvailable(RoleManager.ROLE_DIALER) == true) {
                        setDefaultDialerLauncher.launch(rm.createRequestRoleIntent(RoleManager.ROLE_DIALER))
                    }
                } else {
                    setDefaultDialerLauncher.launch(this)
                }
            } else {
                // Handle the case when the intent cannot be resolved
            }
        }
    }

    private val grandPopUpPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Người dùng đã cấp quyền, thực hiện hành động tương ứng ở đây
                // Ví dụ: Hiển thị thông báo cấp quyền thành công
                checkToggle()
                Toast.makeText(this, "Quyền được cấp thành công!", Toast.LENGTH_SHORT).show()
            } else {
                // Người dùng từ chối cấp quyền hoặc có lỗi xảy ra
                // Ví dụ: Hiển thị thông báo cấp quyền không thành công
                Toast.makeText(this, "Cấp quyền không thành công!", Toast.LENGTH_SHORT).show()
            }
        }

    private fun canDrawOverlayViews(): Boolean {
        val con: Context = this
        return try {
            Settings.canDrawOverlays(con)
        } catch (e: NoSuchMethodError) {
            canDrawOverlaysUsingReflection(con)
        }
    }

    private fun canDrawOverlaysUsingReflection(context: Context): Boolean {
        try {
            val manager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val clazz: Class<*> = AppOpsManager::class.java
            val dispatchMethod =
                clazz.getMethod("checkOp", Int::class.java, Int::class.java, String::class.java)
            // AppOpsManager.OP_SYSTEM_ALERT_WINDOW = 24
            val mode = dispatchMethod.invoke(
                manager,
                24,
                Binder.getCallingUid(),
                context.applicationContext.packageName
            ) as Int

            return AppOpsManager.MODE_ALLOWED == mode
        } catch (e: Exception) {
            return false
        }
    }


    private fun permissionCallDefault() {
        setDefaultDialerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                if (!canDrawOverlayViews() && DeviceUtil.isXiaomi()) {
                    grandPopUpPermissionLauncher.launchAppPermissionsSettings(packageName)
                } else {
                    checkToggle()
                }
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.permissions_denied), Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}