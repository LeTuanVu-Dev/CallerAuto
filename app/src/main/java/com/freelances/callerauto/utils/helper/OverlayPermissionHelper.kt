package com.freelances.callerauto.utils.helper

import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.provider.Settings

object OverlayPermissionHelper {

    private const val OP_SYSTEM_ALERT_WINDOW = 24

    fun hasOverlayPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                Settings.canDrawOverlays(context)
            } catch (e: NoSuchMethodError) {
                canDrawOverlaysUsingReflection(context)
            }
        } else {
            true // dưới Android M thì không cần xin permission này
        }
    }

    fun requestOverlayPermission(activity: Activity) {
        if (!hasOverlayPermission(activity)) {
            try {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${activity.packageName}")
                )
                activity.startActivity(intent)
            } catch (e: Exception) {
                // Nếu mở intent Android gốc thất bại (MIUI chẳng hạn), dùng fallback MIUI
                openMiuiPermissionSettings(activity)
            }
        }
    }

    private fun canDrawOverlaysUsingReflection(context: Context): Boolean {
        return try {
            val manager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val clazz: Class<*> = AppOpsManager::class.java
            val method = clazz.getMethod("checkOp", Int::class.java, Int::class.java, String::class.java)
            val result = method.invoke(manager, OP_SYSTEM_ALERT_WINDOW, Binder.getCallingUid(), context.packageName) as Int
            result == AppOpsManager.MODE_ALLOWED
        } catch (e: Exception) {
            false
        }
    }

    private fun openMiuiPermissionSettings(context: Context) {
        try {
            val intent = Intent("miui.intent.action.APP_PERM_EDITOR").apply {
                setClassName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.permissions.PermissionsEditorActivity"
                )
                putExtra("extra_pkgname", context.packageName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
