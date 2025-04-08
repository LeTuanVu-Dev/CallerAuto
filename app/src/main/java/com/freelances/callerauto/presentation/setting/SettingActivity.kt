package com.freelances.callerauto.presentation.setting

import android.view.View
import android.widget.ImageView
import com.freelances.callerauto.R
import com.freelances.callerauto.databinding.ActivitySettingBinding
import com.freelances.callerauto.presentation.bases.BaseActivity
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

    }

    private fun initAction() {
        binding.apply {
            ivBack.safeClick { finish() }

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
        }
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

}