package com.freelances.callerauto.presentation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.freelances.callerauto.R
import com.freelances.callerauto.databinding.DialogInputTimeBinding
import com.freelances.callerauto.databinding.DialogRenameBinding
import com.freelances.callerauto.presentation.bases.BaseDialogFragment
import com.freelances.callerauto.utils.ext.safeClick
import com.freelances.callerauto.utils.ext.tap

class RenameDialog : BaseDialogFragment<DialogRenameBinding>() {
    private var submitAction: ((String) -> Unit)? = null

    companion object {
        fun newInstance(
            submitAction: ((String) -> Unit)
        ): RenameDialog {
            val dialog = RenameDialog()
            dialog.submitAction = submitAction
            return dialog
        }
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogRenameBinding {
        return DialogRenameBinding.inflate(inflater, container, false)
    }

    override fun getLayout(): Int {
        return R.layout.dialog_input_time
    }
    private var currentText: String? = null

    fun setTimeCurrent(text: String) {
        currentText = text
    }
    override fun updateUI(savedInstanceState: Bundle?) {
        currentText?.let {
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