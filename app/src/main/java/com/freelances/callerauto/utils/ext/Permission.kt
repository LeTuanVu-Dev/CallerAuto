package com.freelances.callerauto.utils.ext

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.text.TextUtils
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.Locale

fun Context.checkPermissionGranted(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

fun Context.arePermissionsGranted(permissions: List<String>): Boolean {
    for (permission in permissions) {
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
    }
    return true
}


var READ_PHONE_PERMISSION = arrayOf(Manifest.permission.READ_PHONE_STATE)
var READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE

fun getReadPhone(): Array<String> {
    return READ_PHONE_PERMISSION
}

fun readPhonePermissionGrant(context: Context): Boolean {
    return allPermissionGrant(context, getReadPhone())
}

fun hasShowRequestPermissionRationale(
    context: Context?,
    vararg permissions: String?
): Boolean {
    if (context != null) {
        for (permission in permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    (context as Activity?)!!,
                    permission!!
                )
            ) {
                return true
            }
        }
    }
    return false
}


private fun allPermissionGrant(context: Context, intArray: Array<String>): Boolean {
    var isGranted = true
    for (permission in intArray) {
        if (ContextCompat.checkSelfPermission(
                context,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            isGranted = false
            break
        }
    }
    return isGranted
}

fun isNotificationServiceEnabled(activity: Activity): Boolean {
    val pkgName: String = activity.packageName
    val flat = Settings.Secure.getString(
        activity.contentResolver,
        "enabled_notification_listeners"
    )
    if (!TextUtils.isEmpty(flat)) {
        val names = flat.split(":").toTypedArray()
        for (i in names.indices) {
            val cn = ComponentName.unflattenFromString(names[i])
            if (cn != null) {
                if (TextUtils.equals(pkgName, cn.packageName)) {
                    return true
                }
            }
        }
    }
    return false
}

fun openAutoStartSettings(context: Context) {
    try {
        val manufacturer = Build.MANUFACTURER.lowercase(Locale.ROOT)
        val intent = Intent()
        when (manufacturer) {
            "xiaomi" -> {
                // Xiaomi
                intent.component = ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity"
                )
            }
            "oppo" -> {
                // Oppo
                intent.component = ComponentName(
                    "com.coloros.safecenter",
                    "com.coloros.safecenter.permission.startup.StartupAppListActivity"
                )
            }
            "vivo" -> {
                // Vivo
                intent.component = ComponentName(
                    "com.iqoo.secure",
                    "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager"
                )
            }
            "sony" -> {
                // Vivo
                intent.component = ComponentName(
                    "com.sonymobile.cta",
                    "com.sonymobile.cta.SomcCTAMainActivity"
                )
            }
            "lg" -> {
                // Vivo
                intent.component = ComponentName(
                    "com.android.settings",
                    "com.android.settings.Settings\$AccessLockSummaryActivity"
                )
            }
            "letv" -> {
                // Vivo
                intent.component = ComponentName(
                    "com.letv.android.letvsafe",
                    "com.letv.android.letvsafe.PermissionAndApps"
                )
            }
            "huawei" -> {
                // Huawei
                intent.component = ComponentName(
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"
                )
            }
            "oneplus" -> {
                // OnePlus (thường dùng chung với OxygenOS)
                intent.component = ComponentName(
                    "com.oneplus.security",
                    "com.oneplus.security.chainlaunch.view.ChainLaunchAppListActivity"
                )
            }
            else -> {
                // Với các hãng khác, mở chung vào Settings
                intent.action = Settings.ACTION_SETTINGS
            }
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            // Nếu không tìm thấy màn hình cài đặt autostart riêng biệt, mở chung Settings
            context.startActivity(Intent(Settings.ACTION_SETTINGS))
        }
    } catch (e: Exception) {
        e.printStackTrace()
        // Nếu xảy ra lỗi, mở chung vào Settings
        context.startActivity(Intent(Settings.ACTION_SETTINGS))
    }
}

@SuppressLint("InlinedApi")
const val postNotification = Manifest.permission.POST_NOTIFICATIONS
const val cameraPermission: String = Manifest.permission.CAMERA

fun getCameraPermission(): Array<String> {
    return arrayOf(cameraPermission)
}
val storagePermission =
    listOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

fun Context.isGrantStoragePermission(): Boolean {
    return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
        Environment.isExternalStorageManager()
    } else {
        arePermissionsGranted(storagePermission)
    }
}

fun Context.isGrantedCamera(): Boolean {
    return checkPermissionGranted(cameraPermission)
}

fun Context.isGrantedPostNotification(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        checkPermissionGranted(postNotification)
    } else true
}
