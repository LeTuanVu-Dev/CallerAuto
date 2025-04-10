package com.freelances.callerauto.utils.helper

import android.telecom.Call
import com.freelances.callerauto.model.GsmCallModel

object CallExtension {
    fun Call.toGsmCall(
        inputName: String? = null,
        inputPhoneNumber: String? = null,
        inputNickname: String? = null
    ): GsmCallModel {
        val nameFromCall = try {
            details?.handle?.schemeSpecificPart ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }

        return GsmCallModel(
            status = state.toGsmCallStatus(),
            displayName = inputName ?: nameFromCall,
            phoneNumber = inputPhoneNumber,
            nickName = inputNickname
        )
    }

    private fun Int.toGsmCallStatus() = when (this) {
        Call.STATE_ACTIVE -> GsmCallModel.Status.ACTIVE
        Call.STATE_RINGING -> GsmCallModel.Status.RINGING
        Call.STATE_CONNECTING -> GsmCallModel.Status.CONNECTING
        Call.STATE_DIALING -> GsmCallModel.Status.DIALING
        Call.STATE_DISCONNECTED -> GsmCallModel.Status.DISCONNECTED
        else -> GsmCallModel.Status.UNKNOWN
    }
}