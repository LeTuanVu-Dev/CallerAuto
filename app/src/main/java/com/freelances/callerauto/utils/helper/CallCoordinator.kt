package com.freelances.callerauto.utils.helper

import android.annotation.SuppressLint
import android.content.Context
import android.provider.CallLog
import com.freelances.callerauto.model.CallLogItem
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

object CallCoordinator {
    private val _onCallFinished = MutableSharedFlow<Boolean>(replay = 0)
    val onCallFinished: SharedFlow<Boolean> get() = _onCallFinished

    suspend fun notifyCallFinished(value: Boolean) {
        _onCallFinished.emit(value)
    }

    @SuppressLint("Range")
    fun getCallLog(context: Context): List<CallLogItem> {
        val callLogItems = mutableListOf<CallLogItem>()

        val cursor = context.contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            arrayOf(
                CallLog.Calls.NUMBER,
                CallLog.Calls.TYPE,
                CallLog.Calls.DATE
            ),
            null,
            null,
            "${CallLog.Calls.DATE} DESC"
        )

        cursor?.use {
            while (it.moveToNext()) {
                val number = it.getString(it.getColumnIndex(CallLog.Calls.NUMBER))
                val type = it.getInt(it.getColumnIndex(CallLog.Calls.TYPE))
                val date = it.getLong(it.getColumnIndex(CallLog.Calls.DATE))

                callLogItems.add(CallLogItem(number, type, date))
            }
        }

        return callLogItems
    }
}
