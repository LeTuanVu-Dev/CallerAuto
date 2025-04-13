package com.freelances.callerauto.utils.device

import android.os.Build

object DeviceUtil {
    const val DEVICE_XIAOMI = "xiaomi"
    fun isMiuiRom(): Boolean {
        return try {
            val systemProperties = Class.forName("android.os.SystemProperties")
            val get = systemProperties.getMethod("get", String::class.java)
            val miuiVersion = get.invoke(systemProperties, "ro.miui.ui.version.name") as String
            miuiVersion.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }
    fun isXiaomiOrMiui(): Boolean {
        val manufacturer = Build.MANUFACTURER.lowercase()
        val brand = Build.BRAND.lowercase()
        val model = Build.MODEL.lowercase()

        // Kiểm tra các dòng máy của Xiaomi
        val isXiaomiDevice = manufacturer.contains("xiaomi") || brand.contains("xiaomi") ||
                manufacturer.contains("redmi") || brand.contains("redmi") ||
                manufacturer.contains("poco") || brand.contains("poco")

        // Kiểm tra ROM MIUI
        val isMiuiRom = try {
            val systemProperties = Class.forName("android.os.SystemProperties")
            val get = systemProperties.getMethod("get", String::class.java)
            val miuiVersion = get.invoke(systemProperties, "ro.miui.ui.version.name") as String
            miuiVersion.isNotEmpty()
        } catch (e: Exception) {
            false
        }

        return isXiaomiDevice || isMiuiRom
    }



    fun isXiaomi(): Boolean {
        return DEVICE_XIAOMI.equals(Build.MANUFACTURER, ignoreCase = true)
    }

}