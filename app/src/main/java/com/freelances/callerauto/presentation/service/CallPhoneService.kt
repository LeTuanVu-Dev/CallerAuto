package com.freelances.callerauto.presentation.service

import android.content.Intent
import android.telecom.Call
import android.telecom.CallAudioState
import android.telecom.InCallService
import com.freelances.callerauto.presentation.call.CallPhoneActivity


class CallPhoneService : InCallService() {

    fun changeStateSpeaker(boolean: Boolean){
        if (boolean){
            setAudioRoute(CallAudioState.ROUTE_SPEAKER)
        }
        else{
            setAudioRoute(CallAudioState.ROUTE_EARPIECE)
        }
    }

    override fun onCallAdded(call: Call?) {
        super.onCallAdded(call)
        call?.registerCallback(callCallback)
        CallPhoneManager.updateCall(call)
        CallPhoneActivity.service = this
        val intent = Intent(this, CallPhoneActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    override fun onCallRemoved(call: Call?) {
        super.onCallRemoved(call)
        call?.unregisterCallback(callCallback)
        CallPhoneManager.updateCall(null)
    }

    private val callCallback = object : Call.Callback() {
        override fun onStateChanged(call: Call, state: Int) {
            CallPhoneManager.updateCall(call)
        }
    }
}