package com.freelances.callerauto.presentation.dialog

import android.content.Context
import com.freelances.callerauto.databinding.DialogCountTimeReCallBinding
import com.freelances.callerauto.presentation.bases.BaseDialog
import com.freelances.callerauto.utils.ext.safeClick
import com.freelances.callerauto.utils.ext.tap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CountTimeReCallDialog(
    ctx: Context,
    private val actionTimeDone: () -> Unit,
    private val onCancelClick: () -> Unit,
) :
    BaseDialog<DialogCountTimeReCallBinding>(ctx, DialogCountTimeReCallBinding::inflate) {

    private var jobEndLifted: Job? = null
    private var currentEndLifted = 0

    fun setTimeCount(time: Int) {
        currentEndLifted = time
    }

    private fun countTimeEndLifted() {
        jobEndLifted?.cancel()
        jobEndLifted = CoroutineScope(Dispatchers.Default + Job()).launch {
            while (isActive) {
                delay(1_000L)
                currentEndLifted--
                withContext(Dispatchers.Main) {
                    binding.tvContent.text = currentEndLifted.toString()
                    if (currentEndLifted == 0) {
                        actionTimeDone()
                        dismiss()
                        cleanUp()
                    }
                }
            }
        }
    }

    private fun cleanUp() {
        jobEndLifted?.cancel()
        jobEndLifted = null
    }

    override fun initView() {
        setContentView(binding.root)
        setCanceledOnTouchOutside(false)
        setCancelable(false)
        countTimeEndLifted()
        binding.buttonNo.tap {
            cleanUp()
            onCancelClick()
            dismiss()
        }

        binding.buttonOk.safeClick {
            dismiss()
        }


        binding.root.tap {
            dismiss()
        }

        binding.ctlDialog.tap {}

    }


}