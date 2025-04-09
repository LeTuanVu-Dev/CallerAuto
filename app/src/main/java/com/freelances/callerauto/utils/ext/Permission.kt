package com.freelances.callerauto.utils.ext

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

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
var CALL_PHONE_PERMISSION = arrayOf(Manifest.permission.CALL_PHONE)
var READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE

fun getReadPhone(): Array<String> {
    return READ_PHONE_PERMISSION
}

fun getCallPhone(): Array<String> {
    return CALL_PHONE_PERMISSION
}

fun readPhonePermissionGrant(context: Context): Boolean {
    return allPermissionGrant(context, getCallPhone())
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

fun Context.getIntentSettingsPermission(): Intent {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts(
        "package",
        packageName, null
    )
    intent.setData(uri)
    return intent
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
    return arePermissionsGranted(storagePermission)
}

fun Context.isGrantedCamera(): Boolean {
    return checkPermissionGranted(cameraPermission)
}

fun Context.isGrantedPostNotification(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        checkPermissionGranted(postNotification)
    } else true
}
