package com.freelances.callerauto.utils.helper

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicBoolean

class NetworkMonitor(context: Context) {

    private val appContext = context.applicationContext
    private val connectivityManager =
        appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private var callback: ConnectivityManager.NetworkCallback? = null
    private val isRegistered = AtomicBoolean(false)

    private val _isNetWorkReady = MutableStateFlow<Boolean?>(null)
    val isNetWorkReady = _isNetWorkReady.asStateFlow()

    fun startListening() {
        if (isRegistered.get()) return

        val cb = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) = updateStatus(true)
            override fun onLost(network: Network) = updateStatus(false)
        }
        callback = cb

        try {
            connectivityManager.registerDefaultNetworkCallback(cb)
            isRegistered.set(true)
            checkNetworkStatus()
        } catch (e: Exception) {
            callback = null
            isRegistered.set(false)
        }
    }

    fun stopListening() {
        if (!isRegistered.get()) {
            _isNetWorkReady.value = null
            return
        }

        val cb = callback ?: return
        try {
            connectivityManager.unregisterNetworkCallback(cb)
        } catch (_: IllegalArgumentException) {
        } catch (_: IllegalStateException) {
        } finally {
            isRegistered.set(false)
            callback = null
            _isNetWorkReady.value = null
        }
    }

    fun checkNetworkStatus() {
        val network = connectivityManager.activeNetwork
        val caps = connectivityManager.getNetworkCapabilities(network)
        val status = caps?.let {
            it.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    (!it.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED).not())
        } ?: false
        updateStatus(status)
    }

    private fun updateStatus(status: Boolean) {
        if (status != _isNetWorkReady.value) {
            _isNetWorkReady.value = status
        }
    }
}