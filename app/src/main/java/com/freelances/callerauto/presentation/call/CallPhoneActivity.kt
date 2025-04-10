package com.freelances.callerauto.presentation.call

import android.content.Context
import android.media.AudioManager
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.freelances.callerauto.R
import com.freelances.callerauto.databinding.ActivityCallPhoneBinding
import com.freelances.callerauto.model.GsmCallModel
import com.freelances.callerauto.presentation.bases.BaseActivity
import com.freelances.callerauto.presentation.service.CallPhoneService
import com.freelances.callerauto.utils.ext.gone
import com.freelances.callerauto.utils.ext.safeClick
import com.freelances.callerauto.utils.ext.visible
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class CallPhoneActivity : BaseActivity<ActivityCallPhoneBinding>(ActivityCallPhoneBinding::inflate) {

    companion object {
        lateinit var service: CallPhoneService
    }
    private var isTimerRunning = false
    private var timerJob: Job? = null
    private var seconds: Long = 0

    private var isMute = false
    private var isSpeak = false

    override fun initViews() {

        binding.apply {
            lnLayoutCalling.gone()
            btnRejectCall.visible()
            btnAcceptCall.visible()

            ivReject.safeClick {
                CallPhoneManager.cancelCall()
            }

            btnRejectCall.safeClick {
                CallPhoneManager.cancelCall()
            }

            btnAcceptCall.setOnClickListener {
                CallPhoneManager.acceptCall()
                btnRejectCall.gone()
                btnAcceptCall.gone()
                lnLayoutCalling.visible()
            }

            ivMute.safeClick {
                isMute = !isMute
                changeMute(isMute)
                binding.ivMute.setImageResource(if(isMute) R.drawable.ic_un_mute else R.drawable.ic_mute)
            }

            ivSpeaker.safeClick {
                isSpeak = !isSpeak
                service.changeStateSpeaker(isSpeak)
                binding.ivSpeaker.setImageResource(if(isSpeak) R.drawable.ic_anoucer else R.drawable.ic_un_anoucer)

            }
        }
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launchWhenStarted {
            CallPhoneManager.callStateFlow.collect { gsmCallModel ->
                gsmCallModel?.let {
                    updateView(it) // 👉 GỌI Ở ĐÂY
                }
            }
        }
    }


    private fun updateView(gsmCallModel: GsmCallModel) {
        binding.textStatus.visibility = when (gsmCallModel.status) {
            GsmCallModel.Status.ACTIVE -> View.GONE
            else -> View.VISIBLE
        }
        binding.textStatus.text = when (gsmCallModel.status) {
            GsmCallModel.Status.CONNECTING -> "Connecting…"
            GsmCallModel.Status.DIALING -> "Calling…"
            GsmCallModel.Status.RINGING -> "Incoming call"
            GsmCallModel.Status.ACTIVE -> ""
            GsmCallModel.Status.DISCONNECTED -> "Finished call"
            GsmCallModel.Status.UNKNOWN -> ""
        }
        binding.textDuration.visibility = when (gsmCallModel.status) {
            GsmCallModel.Status.ACTIVE -> View.VISIBLE
            else -> View.GONE
        }

        binding.lnLayoutCalling.visibility = when (gsmCallModel.status) {
            GsmCallModel.Status.ACTIVE -> View.VISIBLE
            GsmCallModel.Status.DIALING -> View.GONE
            else -> View.GONE
        }

        binding.btnRejectCall.visibility = when (gsmCallModel.status) {
            GsmCallModel.Status.DISCONNECTED -> View.GONE
            else -> View.VISIBLE
        }

        if (gsmCallModel.status == GsmCallModel.Status.DISCONNECTED) {
            binding.btnRejectCall.postDelayed({ finish() }, 3000)
        }

        when (gsmCallModel.status) {
            GsmCallModel.Status.ACTIVE -> {
                if (!isTimerRunning) {
                    startTimer()
                    isTimerRunning = true
                }
            }
            GsmCallModel.Status.DISCONNECTED -> {
                stopTimer()
                isTimerRunning = false
            }
            else -> Unit
        }

        binding.textDisplayName.text = gsmCallModel.nickName ?:  gsmCallModel.displayName ?: "Unknown"
        binding.phoneNumber.text = gsmCallModel.phoneNumber ?: "Unknown"

        binding.btnAcceptCall.visibility = when (gsmCallModel.status) {
            GsmCallModel.Status.RINGING -> View.VISIBLE
            else -> View.GONE
        }
    }

    private fun startTimer() {
        seconds = 0
        timerJob = lifecycleScope.launch {
            while (isActive) {
                binding.textDuration.text = seconds.toDurationString()
                delay(1000)
                seconds++
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    override fun onDestroy() {
        stopTimer()
        super.onDestroy()
    }
    private fun Long.toDurationString() =
        String.format("%02d:%02d:%02d", this / 3600, (this % 3600) / 60, this % 60)


    private fun changeMute(isMute:Boolean) {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.mode = if (isMute) AudioManager.MODE_IN_COMMUNICATION else AudioManager.MODE_NORMAL
        audioManager.isMicrophoneMute = isMute
    }

}