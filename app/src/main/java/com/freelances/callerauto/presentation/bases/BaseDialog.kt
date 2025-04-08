package com.freelances.callerauto.presentation.bases

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.viewbinding.ViewBinding
import com.freelances.callerauto.R


abstract class BaseDialog<VB : ViewBinding>(context: Context, private val inflate: (LayoutInflater, ViewGroup?, Boolean) -> VB ) :
    Dialog(context, R.style.BaseDialog) {
    private var _binding: VB? = null
    protected val binding get() = _binding!!
    abstract fun initView()
    open fun isFullHeight () : Boolean = false
    open fun isFullWidth () : Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setLayout(
            if(isFullWidth()) WindowManager.LayoutParams.MATCH_PARENT else WindowManager.LayoutParams.WRAP_CONTENT,
            if(isFullHeight()) WindowManager.LayoutParams.MATCH_PARENT else WindowManager.LayoutParams.WRAP_CONTENT,
        )

        _binding = inflate(layoutInflater, null, false)
        setContentView(binding.root)
        setCancelable(isCancelableDialog())
        setCanceledOnTouchOutside(isCancelOnTouchOutside())
        initView()

    }

    open fun isCancelableDialog(): Boolean {
        return true
    }

    open fun isCancelOnTouchOutside(): Boolean {
        return true
    }

    override fun onDetachedFromWindow() {
        _binding = null
        super.onDetachedFromWindow()
    }

    fun dismissDialog() {
        onDismissInternal()
        dismiss()
    }

    open fun onDismissInternal() {}

}