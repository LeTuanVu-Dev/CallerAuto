package com.freelances.callerauto.presentation.bases

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.RelativeLayout
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.freelances.callerauto.R
import com.freelances.callerauto.utils.ext.hideSystemBar

abstract class BaseDialogFragment<V : ViewBinding> : DialogFragment() {
    private var _binding: V? = null
    val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val root = RelativeLayout(activity)
        return setupDialogContentView(root)

    }

    private fun setupDialogContentView(root: View): Dialog {
        root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val dialog = if (isFullHeight()) Dialog(
            activity as FragmentActivity, R.style.AppTheme_DialogFragmentFullScreen
        ) else Dialog(activity as FragmentActivity)
        val screenWidth = resources.displayMetrics.widthPixels
        val dialogWidth = (screenWidth * 0.9).toInt()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(root)
        dialog.setCanceledOnTouchOutside(isEnableCancelOnTouchOutside())
        dialog.window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.setLayout(
                dialogWidth,
                if (isFullHeight()) ViewGroup.LayoutParams.MATCH_PARENT else ViewGroup.LayoutParams.WRAP_CONTENT,
            )
            if (isFullHeight()) {
                setFullScreen(it)
                val context = context
                if (context != null) {
                    val colorStatusBar = ContextCompat.getColor(context, getStatusBarColor())
                    it.statusBarColor = colorStatusBar
                    val windowInsetsController = WindowCompat.getInsetsController(it, it.decorView)
                    windowInsetsController.isAppearanceLightStatusBars =
                        isColorLight(colorStatusBar)
                }
            }
        }

        return dialog
    }

    abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): V

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = inflateBinding(inflater, container)
        return binding.root
    }

    abstract fun getLayout(): Int


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = isCancelableOnBackPress()
        updateUI(savedInstanceState)
        observerData()
        onClickView()
    }

    open fun observerData() {}

    fun show(fm: FragmentManager) = apply {
        show(fm, this::class.java.canonicalName)
    }

    @ColorRes
    open fun getStatusBarColor(): Int {
        return R.color.white
    }

    private fun isColorDark(@ColorInt color: Int): Boolean {
        val darkness =
            1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return darkness >= 0.5
    }

    private fun isColorLight(@ColorInt color: Int): Boolean {
        return !isColorDark(color)
    }


    open fun isFullWidth(): Boolean = true
    open fun isFullHeight(): Boolean = false

    open fun isCancelableOnBackPress(): Boolean {
        return true
    }

    open fun isEnableCancelOnTouchOutside(): Boolean {
        return true
    }

    abstract fun updateUI(savedInstanceState: Bundle?)
    open fun onClickView() {}

    private fun setFullScreen(window: Window) {
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.transparent)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
        val layoutParams = window.attributes
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            layoutParams.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        window.attributes = layoutParams
    }

    private lateinit var myContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.myContext = context
    }

    protected val baseContext: Context
        get() = context ?: activity ?: myContext

    protected val baseActivity: BaseActivity<*>
        get() = baseContext as BaseActivity<*>

    override fun onResume() {
        super.onResume()
        dialog?.window?.hideSystemBar()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
