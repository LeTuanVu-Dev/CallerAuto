package com.freelances.callerauto.presentation.call

import android.content.Context
import android.media.AudioManager
import android.view.View
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.freelances.callerauto.R
import com.freelances.callerauto.databinding.ActivityCallPhoneBinding
import com.freelances.callerauto.model.GsmCallModel
import com.freelances.callerauto.presentation.bases.BaseActivity
import com.freelances.callerauto.presentation.service.CallPhoneService
import com.freelances.callerauto.utils.ext.formatToHourMinuteSecond
import com.freelances.callerauto.utils.ext.gone
import com.freelances.callerauto.utils.ext.safeClick
import com.freelances.callerauto.utils.ext.visible
import com.freelances.callerauto.utils.helper.CallCoordinator
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class CallPhoneActivity :
    BaseActivity<ActivityCallPhoneBinding>(ActivityCallPhoneBinding::inflate) {

    companion object {
        lateinit var service: CallPhoneService
    }

    private var isTimerRunning = false
    private var timerJob: Job? = null
    private var seconds: Long = 0

    private var isMute = false
    private var isSpeak = false
    private var currentEndLifted = 0
    private var jobEndLifted: Job? = null

    override fun initViews() {
        setUpMute()
        setUpSpeak()
        setUpEndWhenLifted()
        setUpEndAuto()
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
                binding.ivMute.setImageResource(if (isMute) R.drawable.ic_un_mute else R.drawable.ic_mute)
            }

            ivSpeaker.safeClick {
                isSpeak = !isSpeak
                service.changeStateSpeaker(isSpeak)
                binding.ivSpeaker.setImageResource(if (isSpeak) R.drawable.ic_anoucer else R.drawable.ic_un_anoucer)

            }
        }
        if (sharedPreference.stateAutoEnd) {
            binding.lnTimeAutoEnd.visible()
            countTimeEndLifted()
        }
    }

    private fun setUpMute() {
        isMute = sharedPreference.stateMuteMicro
        changeMute(isMute)
        binding.ivMute.setImageResource(if (isMute) R.drawable.ic_un_mute else R.drawable.ic_mute)
    }

    private fun setUpEndWhenLifted() {
        if (sharedPreference.stateEndLifted && !sharedPreference.stateAutoEnd) {
            binding.lnTimeAutoEnd.visible()
            currentEndLifted = sharedPreference.currentTimerEndLifted
            binding.tvTimeEndAuto.text =
                formatToHourMinuteSecond(sharedPreference.currentTimerEndLifted)
        }
    }

    private fun setUpEndAuto() {
        if (sharedPreference.stateAutoEnd) {
            binding.lnTimeAutoEnd.visible()
            currentEndLifted = sharedPreference.currentTimerEndAuto
            binding.tvTimeEndAuto.text =
                formatToHourMinuteSecond(sharedPreference.currentTimerEndAuto)
        }
    }

    private fun countTimeEndLifted() {
        jobEndLifted?.cancel()
        jobEndLifted = lifecycleScope.launch {
            delay(1_000L)
            currentEndLifted--
            binding.tvTimeEndAuto.text = formatToHourMinuteSecond(currentEndLifted)
            if (currentEndLifted == 0){
                CallPhoneManager.cancelCall()
            }
        }
    }

    private fun setUpSpeak() {
        isSpeak = sharedPreference.stateEnableSpeaker
        service.changeStateSpeaker(isSpeak)
        binding.ivSpeaker.setImageResource(if (isSpeak) R.drawable.ic_anoucer else R.drawable.ic_un_anoucer)
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
        binding.textDisplayName.isInvisible = sharedPreference.stateHideCallName
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
            GsmCallModel.Status.DIALING -> View.VISIBLE
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
                if (sharedPreference.stateEndLifted && !sharedPreference.stateAutoEnd) {
                    countTimeEndLifted()
                }

                if (!isTimerRunning) {
                    startTimer()
                    isTimerRunning = true
                }
            }
            GsmCallModel.Status.RINGING -> {
                if (sharedPreference.stateRejectCalls){
                    CallPhoneManager.cancelCall()
                }
            }

            GsmCallModel.Status.DISCONNECTED -> {
                lifecycleScope.launch {
                    CallCoordinator.notifyCallFinished(true)
                }
                stopTimer()
                isTimerRunning = false
            }

            else -> Unit
        }

        binding.textDisplayName.text = gsmCallModel.displayName ?: "Unknown"
        binding.phoneNumber.text = gsmCallModel.nickName?.split(",")?.first() ?: "Unknown"

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
        jobEndLifted?.cancel()
        stopTimer()
        super.onDestroy()
    }

    private fun Long.toDurationString() =
        String.format("%02d:%02d:%02d", this / 3600, (this % 3600) / 60, this % 60)


    private fun changeMute(isMute: Boolean) {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.mode =
            if (isMute) AudioManager.MODE_IN_COMMUNICATION else AudioManager.MODE_NORMAL
        audioManager.isMicrophoneMute = isMute
    }

}