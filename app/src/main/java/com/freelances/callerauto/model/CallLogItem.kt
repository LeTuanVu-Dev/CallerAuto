package com.freelances.callerauto.model

import android.provider.CallLog
import java.text.SimpleDateFormat
import java.util.Locale

data class CallLogItem(
    val number: String,
    val type: Int,
    val date: Long
)
{
    val callTypeText: String
        get() = when (type) {
            CallLog.Calls.INCOMING_TYPE -> "Gọi đến"
            CallLog.Calls.OUTGOING_TYPE -> "Gọi đi"
            CallLog.Calls.MISSED_TYPE -> "Nhỡ"
            CallLog.Calls.VOICEMAIL_TYPE -> "Thư thoại"
            CallLog.Calls.REJECTED_TYPE -> "Từ chối"
            CallLog.Calls.BLOCKED_TYPE -> "Bị chặn"
            CallLog.Calls.ANSWERED_EXTERNALLY_TYPE -> "Đã trả lời ngoài thiết bị"
            else -> "Không rõ"
        }
    val formattedDate: String
        get() {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            return sdf.format(date)
        }
}