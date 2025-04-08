package com.freelances.callerauto.remoteconfig

import android.util.Log
import com.freelances.callerauto.utils.preference.AppSharedPreference
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.BuildConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull


class RemoteConfig(
    private val appSharedPreference: AppSharedPreference
) {
    companion object {
        private const val TAG = "CompositeRemoteRepository"
        private const val TIMEOUT_DURATION = 30_000L
    }

    init {
        val configSettings = remoteConfigSettings {
            fetchTimeoutInSeconds = 30L
            minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 0L else 3600L
        }
        Firebase.remoteConfig.apply {
            setConfigSettingsAsync(configSettings)
        }
    }

    suspend fun fetchRemoteData(): FirebaseSetupResult = withTimeoutOrNull(TIMEOUT_DURATION) {
        try {
            val fetchResult = Firebase.remoteConfig.fetchAndActivate().await()
            if (fetchResult) {
                syncData()
            }
            FirebaseSetupResult.FetchCompleted
        } catch (e: Exception) {
            FirebaseSetupResult.FetchCompleted
        }
    } ?: FirebaseSetupResult.FetchCompleted


    private fun syncData() = try {
        appSharedPreference.sync()
    } catch (e: Exception) {
        Log.e(TAG, "syncData: Exception ${e.message}")
    }

}

sealed class FirebaseSetupResult {
    data object FetchCompleted : FirebaseSetupResult()
    data object FetchFailed : FirebaseSetupResult()
    data object Fetching : FirebaseSetupResult()
}