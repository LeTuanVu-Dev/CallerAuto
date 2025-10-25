package com.freelances.callerauto.utils.helper

import android.content.Context
import android.provider.Settings
import com.freelances.callerauto.model.KeyModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore

object DeviceKeyManager {

    private fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    /**
     * Kiểm tra key người dùng nhập:
     * - Nếu key tồn tại và countUserLogin == 0 => claim (set countUserLogin = 1)
     * - Nếu countUserLogin == 1 => cho phép login lại
     * - Không tự tạo key mới
     */
    fun validateAndClaimKey(
        context: Context,
        inputKey: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val key = inputKey.trim().uppercase()
        if (key.isEmpty()) {
            onResult(false, "Key trống")
            return
        }

        val db = Firebase.firestore
        val ref = db.collection("keys").document(key)

        db.runTransaction { tx ->
            val snapshot = tx.get(ref)
            if (!snapshot.exists()) {
                throw IllegalStateException("Key không tồn tại")
            }

            val model = snapshot.toObject(KeyModel::class.java)
                ?: throw IllegalStateException("Dữ liệu key không hợp lệ")

            return@runTransaction when {
                model.countUserLogin == 0 -> {
                    // claim key lần đầu
                    tx.update(ref, mapOf(
                        "countUserLogin" to 1
                    ))
                    true
                }

                model.countUserLogin == 1 -> {
                    // key đã được dùng, nhưng vẫn cho login lại
                    true
                }

                else -> {
                    throw IllegalStateException("Key không hợp lệ")
                }
            }
        }.addOnSuccessListener {
            onResult(true, "OK")
        }.addOnFailureListener { e ->
            onResult(false, e.message)
        }
    }

    /**
     * Dùng khi mở lại app:
     * - Nếu key tồn tại và countUserLogin == 1 => cho phép
     */
    fun verifySavedKey(
        context: Context,
        savedKey: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val key = savedKey.trim().uppercase()
        if (key.isEmpty()) {
            onResult(false, "Key trống")
            return
        }

        val db = Firebase.firestore
        val ref = db.collection("keys").document(key)

        ref.get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) {
                    onResult(false, "Key không tồn tại")
                    return@addOnSuccessListener
                }

                val model = doc.toObject(KeyModel::class.java)
                if (model != null && model.countUserLogin == 1) {
                    onResult(true, "OK")
                } else {
                    onResult(false, "Key chưa được claim hoặc không hợp lệ")
                }
            }
            .addOnFailureListener { e ->
                onResult(false, e.message)
            }
    }
}
