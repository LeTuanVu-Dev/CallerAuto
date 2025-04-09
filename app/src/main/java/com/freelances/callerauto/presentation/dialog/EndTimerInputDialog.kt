package com.freelances.callerauto.presentation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.freelances.callerauto.R
import com.freelances.callerauto.databinding.DialogInputTimeBinding
import com.freelances.callerauto.presentation.bases.BaseDialogFragment
import com.freelances.callerauto.utils.ext.safeClick
import com.freelances.callerauto.utils.ext.tap

class EndTimerInputDialog : BaseDialogFragment<DialogInputTimeBinding>() {
    private var submitAction: ((String) -> Unit)? = null

    companion object {
        fun newInstance(
            submitAction: ((String) -> Unit)
        ): EndTimerInputDialog {
            val dialog = EndTimerInputDialog()
            dialog.submitAction = submitAction
            return dialog
        }
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogInputTimeBinding {
        return DialogInputTimeBinding.inflate(inflater, container, false)
    }

    override fun getLayout(): Int {
        return R.layout.dialog_input_time
    }
    private var currentTime: String? = null

    fun setTimeCurrent(text: String) {
        currentTime = text
    }
    override fun updateUI(savedInstanceState: Bundle?) {
        currentTime?.let {
            binding.edInput.setText(it)
        }
        binding.btOk.safeClick {
            submitAction?.invoke(binding.edInput.text.toString().trim())
            dismiss()
        }
        binding.btCancel.tap {
            dismiss()
        }
    }
}