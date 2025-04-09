package com.freelances.callerauto.utils.device

import android.os.Build

object DeviceUtil {
    const val DEVICE_XIAOMI = "xiaomi"
    fun isXiaomi(): Boolean {
        return DEVICE_XIAOMI.equals(Build.MANUFACTURER, ignoreCase = true)
    }

}