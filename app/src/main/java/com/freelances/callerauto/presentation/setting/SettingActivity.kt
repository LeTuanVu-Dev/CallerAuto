package com.freelances.callerauto.presentation.setting

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.freelances.callerauto.R
import com.freelances.callerauto.databinding.ActivitySettingBinding
import com.freelances.callerauto.presentation.bases.BaseActivity
import com.freelances.callerauto.presentation.dialog.EndTimerInputDialog
import com.freelances.callerauto.utils.ext.safeClick
import com.freelances.callerauto.utils.ext.tap

class SettingActivity : BaseActivity<ActivitySettingBinding>(ActivitySettingBinding::inflate) {
    override fun initViews() {
        if (resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
            binding.ivBack.scaleX = -1f // Lật ngược khi RTL
        } else {
            binding.ivBack.scaleX = 1f // Giữ nguyên khi LTR
        }
        initData()
        initAction()
    }

    private fun initData() {
        updateNumberStartCall(sharedPreference.currentAutoCallPosition.toString())
        updateNumberRepeat(sharedPreference.currentNumberRepeat.toString())
        updateTimerAuto(sharedPreference.currentTimerEndAuto.toString())
        updateTimerWait(sharedPreference.currentTimerEndWaiting.toString())
        binding.apply {
            setUpState(sharedPreference.stateEndLifted, ivToggleEndLifted)
            setUpState(sharedPreference.stateMuteMicro, ivToggleMicro)
            setUpState(sharedPreference.stateEnableSpeaker, ivToggleSpeak)
            setUpState(sharedPreference.stateHideCallName, ivToggleCallName)
            setUpState(sharedPreference.stateAutoEnd, ivToggleAutoEnd)
            setUpState(sharedPreference.stateRejectCalls, ivToggleReject)
            setUpState(sharedPreference.stateAnonymousCall, ivToggleAnonymous)
            setUpState(sharedPreference.stateRepeatList, ivToggleRepeat)
            setUpState(sharedPreference.stateRedial, ivToggleRedial)
        }
        binding.tvEndTime2.setTextColor(
            ContextCompat.getColor(
                this@SettingActivity,
                setEnableView(sharedPreference.stateAutoEnd)
            )
        )
        binding.tvSubEndTime2.setTextColor(
            ContextCompat.getColor(
                this@SettingActivity,
                setEnableSubView(sharedPreference.stateAutoEnd)
            )
        )
    }

    private fun setSimSelection(tvSelected: TextView, tvUnselected: TextView, simType: Int) {
        sharedPreference.currentSimType = simType
        tvSelected.setBackgroundResource(R.drawable.bg_dialog_negative_button)
        tvSelected.setTextColor(resources.getColor(R.color.black))
        tvUnselected.setBackgroundResource(R.drawable.bg_dialog_outline_button)
        tvUnselected.setTextColor(resources.getColor(R.color.white))
    }

    private fun initAction() {
        binding.apply {
            ivBack.safeClick { finish() }
            tvSim1.tap {
                setSimSelection(tvSim1, tvSim2, 0)
            }

            tvSim2.tap {
                setSimSelection(tvSim2, tvSim1, 1)
            }

            ivToggleEndLifted.tap {
                toggleState(
                    currentState = sharedPreference.stateEndLifted,
                    onToggle = { sharedPreference.stateEndLifted = it },
                    imageView = ivToggleEndLifted
                )
            }

            ivToggleMicro.tap {
                toggleState(
                    currentState = sharedPreference.stateMuteMicro,
                    onToggle = { sharedPreference.stateMuteMicro = it },
                    imageView = ivToggleMicro
                )
            }

            ivToggleSpeak.tap {
                toggleState(
                    currentState = sharedPreference.stateEnableSpeaker,
                    onToggle = { sharedPreference.stateEnableSpeaker = it },
                    imageView = ivToggleSpeak
                )
            }

            ivToggleCallName.tap {
                toggleState(
                    currentState = sharedPreference.stateHideCallName,
                    onToggle = { sharedPreference.stateHideCallName = it },
                    imageView = ivToggleCallName
                )
            }

            ivToggleAutoEnd.tap {
                toggleState(
                    currentState = sharedPreference.stateAutoEnd,
                    onToggle = { sharedPreference.stateAutoEnd = it },
                    imageView = ivToggleAutoEnd
                )
                tvEndTime2.setTextColor(
                    ContextCompat.getColor(
                        this@SettingActivity,
                        setEnableView(sharedPreference.stateAutoEnd)
                    )
                )
                tvSubEndTime2.setTextColor(
                    ContextCompat.getColor(
                        this@SettingActivity,
                        setEnableSubView(sharedPreference.stateAutoEnd)
                    )
                )
            }

            ivToggleReject.tap {
                toggleState(
                    currentState = sharedPreference.stateRejectCalls,
                    onToggle = { sharedPreference.stateRejectCalls = it },
                    imageView = ivToggleReject
                )

            }

            ivToggleAnonymous.tap {
                toggleState(
                    currentState = sharedPreference.stateAnonymousCall,
                    onToggle = { sharedPreference.stateAnonymousCall = it },
                    imageView = ivToggleAnonymous
                )
            }

            ivToggleRepeat.tap {
                toggleState(
                    currentState = sharedPreference.stateRepeatList,
                    onToggle = { sharedPreference.stateRepeatList = it },
                    imageView = ivToggleRepeat
                )
            }

            ivToggleRedial.tap {
                toggleState(
                    currentState = sharedPreference.stateRedial,
                    onToggle = { sharedPreference.stateRedial = it },
                    imageView = ivToggleRedial
                )
            }

            lnNumberRepeat.safeClick {
                showInputTimerDialog(sharedPreference.currentNumberRepeat.toString())
            }

            lnTimerWait.safeClick {
                showInputTimerWaitDialog(sharedPreference.currentTimerEndWaiting.toString())
            }

            lnTimeAuto.safeClick {
                if (!sharedPreference.stateAutoEnd) return@safeClick
                showInputTimerAutoDialog(sharedPreference.currentTimerEndAuto.toString())
            }

            lnStartCallFromPosition.safeClick {
                showInputPositionStartDialog(sharedPreference.currentAutoCallPosition.toString())
            }

        }
    }

    private val dialogInputStartCallPositionListener by lazy {
        EndTimerInputDialog.newInstance { timer ->
            updateNumberStartCall(timer)
        }
    }

    private val dialogInputTimerLiftedListener by lazy {
        EndTimerInputDialog.newInstance { timer ->
            updateNumberRepeat(timer)
        }
    }

    private val dialogInputTimerAutoListener by lazy {
        EndTimerInputDialog.newInstance { timer ->
            updateTimerAuto(timer)
        }
    }

    private val dialogInputTimerWaitListener by lazy {
        EndTimerInputDialog.newInstance { timer ->
            updateTimerWait(timer)
        }
    }

    private fun updateNumberStartCall(timer: String) {
        sharedPreference.currentAutoCallPosition = timer.toInt()
        binding.tvCurrentPosition.text = timer
    }

    private fun updateNumberRepeat(timer: String) {
        sharedPreference.currentNumberRepeat = timer.toInt()
        binding.tvNumberRepeat.text = timer
    }

    private fun updateTimerAuto(timer: String) {
        sharedPreference.currentTimerEndAuto = timer.toInt()
        binding.tvSubEndTime2.text = timer + " ${getString(R.string.seconds)}"
    }

    private fun updateTimerWait(timer: String) {
        sharedPreference.currentTimerEndWaiting = timer.toInt()
        binding.tvTimerWait.text = timer + " ${getString(R.string.seconds)}"
    }


    private fun showInputPositionStartDialog(timerCurrent: String) {
        if (!dialogInputStartCallPositionListener.isAdded) {
            dialogInputStartCallPositionListener.setTimeCurrent(timerCurrent)
            dialogInputStartCallPositionListener.show(
                supportFragmentManager,
                "show_gotoSetting_listener"
            )
        }
    }

    private fun showInputTimerDialog(timerCurrent: String) {
        if (!dialogInputTimerLiftedListener.isAdded) {
            dialogInputTimerLiftedListener.setTimeCurrent(timerCurrent)
            dialogInputTimerLiftedListener.show(
                supportFragmentManager,
                "show_gotoSetting_listener"
            )
        }
    }

    private fun showInputTimerAutoDialog(timerCurrent: String) {
        if (!dialogInputTimerAutoListener.isAdded) {
            dialogInputTimerAutoListener.setTimeCurrent(timerCurrent)
            dialogInputTimerAutoListener.show(
                supportFragmentManager,
                "show_gotoSetting_listener"
            )
        }
    }

    private fun showInputTimerWaitDialog(timerCurrent: String) {
        if (!dialogInputTimerWaitListener.isAdded) {
            dialogInputTimerWaitListener.setTimeCurrent(timerCurrent)
            dialogInputTimerWaitListener.show(
                supportFragmentManager,
                "show_gotoSetting_listener"
            )
        }
    }


    private fun setEnableView(isShow: Boolean): Int {
        val endTimeColor = if (isShow) {
            R.color.white
        } else {
            R.color.color_EBEBF5_30
        }
        return endTimeColor

    }

    private fun setEnableSubView(isShow: Boolean): Int {
        val subEndTimeColor = if (isShow) {
            R.color.color_A9AEB8
        } else {
            R.color.color_EBEBF5_30
        }
        return subEndTimeColor
    }

    private fun toggleState(
        currentState: Boolean,
        onToggle: (Boolean) -> Unit,
        imageView: ImageView
    ) {
        val newState = !currentState
        onToggle(newState)
        imageView.setImageResource(if (newState) R.drawable.ic_toggle_on else R.drawable.ic_toggle_off)
    }

    private fun setUpState(currentState: Boolean, imageView: ImageView) {
        imageView.setImageResource(
            if (currentState) R.drawable.ic_toggle_on else R.drawable.ic_toggle_off
        )
    }


}