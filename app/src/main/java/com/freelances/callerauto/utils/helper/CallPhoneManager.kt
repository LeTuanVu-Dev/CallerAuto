import android.telecom.Call
import android.util.Log
import com.freelances.callerauto.model.GsmCallModel
import com.freelances.callerauto.utils.helper.CallExtension.toGsmCall
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object CallPhoneManager {

    private const val LOG_TAG = "CallManager"

    private val _callStateFlow = MutableStateFlow<GsmCallModel?>(null)
    val callStateFlow: StateFlow<GsmCallModel?> = _callStateFlow

    private var currentCall: Call? = null

    fun updateCall(call: Call?) {
        currentCall = call
        call?.let {
            _callStateFlow.value = it.toGsmCall()
        }
    }

    fun cancelCall() {
        currentCall?.let {
            when (it.state) {
                Call.STATE_RINGING -> rejectCall()
                else -> disconnectCall()
            }
        }
    }

    fun acceptCall() {
        Log.i(LOG_TAG, "acceptCall")
        currentCall?.let {
            it.answer(it.details.videoState)
        }
    }

    private fun rejectCall() {
        Log.i(LOG_TAG, "rejectCall")
        currentCall?.reject(false, "")
    }

    private fun disconnectCall() {
        Log.i(LOG_TAG, "disconnectCall")
        currentCall?.disconnect()
    }
}
