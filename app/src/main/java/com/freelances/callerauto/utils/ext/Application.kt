package com.freelances.callerauto.utils.ext

import android.app.Application
import android.content.Context
import java.util.Locale

private var application: Application? = null

fun setApplicationContext(context: Application) {
    application = context
}

fun getApplicationContext() = application ?:throw java.lang.RuntimeException("Application context mustn't null")

fun getContextWithUpdatedLocale(context: Context, locale: Locale): Context {
    val config = context.resources.configuration
    config.setLocale(locale)
    return context.createConfigurationContext(config)
}