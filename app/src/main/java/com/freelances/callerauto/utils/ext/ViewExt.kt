package com.freelances.callerauto.utils.ext

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.View

fun View.gone() {
    this.visibility = View.GONE
}


fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun Context.openWifiSettings() {
    startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
}
fun View.visible() {
    this.visibility = View.VISIBLE
}
