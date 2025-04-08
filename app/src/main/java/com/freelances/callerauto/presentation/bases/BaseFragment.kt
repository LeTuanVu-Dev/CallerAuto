package com.freelances.callerauto.presentation.bases

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ScrollView
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.freelances.callerauto.utils.ext.hideKeyboard
import com.freelances.callerauto.utils.ext.isRTL
import com.freelances.callerauto.utils.preference.AppSharedPreference
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.createScope
import org.koin.core.component.inject
import org.koin.core.scope.Scope
import kotlin.math.abs

abstract class BaseFragment<VB : ViewBinding>(
    private val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB
) : Fragment(), KoinScopeComponent {

    override val scope: Scope = createScope(this)
    protected val sharedPreference: AppSharedPreference by inject()
    private var _binding: VB? = null
    protected val binding get() = _binding!!
    protected val TAG = this::class.java.simpleName

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = bindingInflater(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLayoutDirection()
        initViews()
        onClickViews()
        setActionKeyboardBelowAndroidQ()
    }
    private var yDown = 0f
    private var isMove = false

    abstract fun initViews()
    open fun onClickViews() {}


    fun hideKeyboardScrollView(scrollView: ScrollView) {
        scrollView.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    yDown = motionEvent.y
                }

                MotionEvent.ACTION_UP -> {
                    if (!isMove) {
                        activity?.hideKeyboard()
                    }
                    isMove = false
                }

                MotionEvent.ACTION_MOVE -> {
                    val yMove = motionEvent.y
                    val distY: Float = yMove - yDown
                    if (abs(distY) >= 10) {
                        isMove = true
                    }
                }
            }
            false
        }
    }

    private fun setActionKeyboardBelowAndroidQ() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            binding.root.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    val currentFocusView = requireActivity().currentFocus
                    if (currentFocusView is EditText) {
                        Log.d("TAG123", "onCreate:ẩn bàn phím ")
                        currentFocusView.clearFocus()
                        hideKeyboard(currentFocusView)
                    }
                }
                false
            }

            setupEditTextFocusListener(binding.root as ViewGroup)
        }
    }

    private fun setupEditTextFocusListener(rootView: ViewGroup) {
        for (i in 0 until rootView.childCount) {
            val child = rootView.getChildAt(i)
            if (child is EditText) {
                child.setOnFocusChangeListener { v, hasFocus ->
                    setupUIonKeyBoardShow(hasFocus)
                }
            } else if (child is ViewGroup) {
                setupEditTextFocusListener(child)  // Đệ quy cho ViewGroup con
            }
        }
    }
    private fun getStatusBarHeight(): Int {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else {
            0
        }
    }
    private fun setupUIonKeyBoardShow(isShow: Boolean) {
        if (isShow) {
            WindowCompat.setDecorFitsSystemWindows(requireActivity().window, true)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            }
            val params = binding.root.layoutParams as ViewGroup.MarginLayoutParams
            params.topMargin = -getStatusBarHeight()
            binding.root.layoutParams = params
        } else {
            WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                requireActivity().window.setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                )
            }
            val params = binding.root.layoutParams as ViewGroup.MarginLayoutParams
            params.topMargin = 0
            binding.root.layoutParams = params
        }
    }

    private fun hideKeyboard(view: View) {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * Hàm điều hướng sang Activity khác (tương tự như BaseActivity)
     */
    private fun setLayoutDirection() {
        binding.root.layoutDirection =
            if (requireContext().isRTL()) View.LAYOUT_DIRECTION_RTL else View.LAYOUT_DIRECTION_LTR
    }


    override fun onDestroyView() {
        scope.close()
        _binding = null
        super.onDestroyView()
    }
}
