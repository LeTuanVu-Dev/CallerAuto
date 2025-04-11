package com.freelances.callerauto.utils.helper

import android.content.Context
import android.provider.Settings
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object DeviceKeyManager {

    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun generateRandomKey(length: Int = 10): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..length).map { chars.random() }.joinToString("")
    }

    fun getOrValidateDeviceKey(
        context: Context,
        inputValue: String?,
        onResult: (success: Boolean, storedValue: String?) -> Unit
    ) {
        val deviceId = getDeviceId(context)
        val keyRef = Firebase.firestore.collection("keys").document(deviceId)

        keyRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val storedValue = document.getString("value")
                if (inputValue != null && inputValue == storedValue) {
                    onResult(true, storedValue)
                } else {
                    onResult(false, storedValue)
                }
            } else {
                val newKey = generateRandomKey()
                val data = mapOf(
                    "value" to newKey,
                    "createdAt" to FieldValue.serverTimestamp()
                )
                keyRef.set(data).addOnSuccessListener {
                    onResult(true, newKey)
                }.addOnFailureListener {
                    onResult(false, null)
                }
            }
        }.addOnFailureListener {
            onResult(false, null)
        }
    }
}
