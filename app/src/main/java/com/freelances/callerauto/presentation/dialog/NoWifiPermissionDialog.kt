package com.freelances.callerauto.presentation.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.freelances.callerauto.R
import com.freelances.callerauto.databinding.DialogNetworkPermissionBinding
import com.freelances.callerauto.presentation.bases.BaseDialogFragment

class NoWifiPermissionDialog :
    BaseDialogFragment<DialogNetworkPermissionBinding>() {
    private var onOkClick: () -> Unit = {}
    private var onDismissListenerPopup: (() -> Unit)? = null

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogNetworkPermissionBinding {
        return DialogNetworkPermissionBinding.inflate(inflater, container, false)
    }


    override fun isCancelableOnBackPress(): Boolean = false
    override fun isEnableCancelOnTouchOutside(): Boolean = false

    override fun getLayout(): Int {
        return R.layout.dialog_network_permission
    }

    override fun updateUI(savedInstanceState: Bundle?) {
        binding.btnOk.setOnClickListener {
            onOkClick()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListenerPopup?.invoke()
    }

    fun setOnOkClick(onOkClick: () -> Unit) = apply {
        this.onOkClick = onOkClick
    }

    fun setOnDismissListener(block: () -> Unit) {
        onDismissListenerPopup = block
    }

}