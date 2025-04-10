package com.freelances.callerauto.presentation.dialog

import android.content.Context
import com.freelances.callerauto.databinding.DialogConfirmDeleteBinding
import com.freelances.callerauto.presentation.bases.BaseDialog
import com.freelances.callerauto.utils.ext.safeClick
import com.freelances.callerauto.utils.ext.tap

class ConfirmDeleteDialog(
    ctx: Context,
    private val onAcceptClick: () -> Unit
) :
    BaseDialog<DialogConfirmDeleteBinding>(ctx, DialogConfirmDeleteBinding::inflate) {


    override fun initView() {
        setContentView(binding.root)
        setCanceledOnTouchOutside(false)
        setCancelable(false)

        binding.buttonNo.tap {
            dismiss()
        }

        binding.buttonOk.safeClick {
            onAcceptClick()
            dismiss()
        }


        binding.root.tap {
            dismiss()
        }

        binding.ctlDialog.tap {}

    }


}