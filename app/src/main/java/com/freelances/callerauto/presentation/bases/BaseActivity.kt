package com.freelances.callerauto.presentation.bases

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewbinding.ViewBinding
import com.freelances.callerauto.R
import com.freelances.callerauto.utils.ext.hideSystemBar
import com.freelances.callerauto.utils.ext.isRTL
import com.freelances.callerauto.utils.ext.lightStatusBar
import com.freelances.callerauto.utils.ext.setFullScreen
import com.freelances.callerauto.utils.helper.LanguageHelper
import com.freelances.callerauto.utils.preference.AppSharedPreference
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.createScope
import org.koin.core.component.inject
import org.koin.core.scope.Scope


abstract class BaseActivity<VB : ViewBinding>(private val bindingInflater: (LayoutInflater) -> VB) :
    AppCompatActivity(), KoinScopeComponent {
    override val scope: Scope = createScope(this)
    protected val sharedPreference: AppSharedPreference by inject()

    private var _binding: VB? = null
    protected val binding get() = _binding!!
    protected val TAG = this::class.java.simpleName
    private val activityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        handleActivityResult(result)
    }
    private lateinit var callback: OnBackPressedCallback
    open fun handleActivityResult(result: ActivityResult) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = bindingInflater(layoutInflater)
        setContentView(binding.root)
        setLayoutDirection()
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val insetTypes =
                if (!isDisplayCutout()) WindowInsetsCompat.Type.displayCutout() or WindowInsetsCompat.Type.systemBars()
                else WindowInsetsCompat.Type.systemBars()
            val systemBars = insets.getInsets(insetTypes)
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.setFullScreen()
        window.hideSystemBar()
        window.lightStatusBar(false)

        initViews()
        onClickViews()
        observerData()

    }

    open fun isDisplayCutout(): Boolean = false
    abstract fun initViews()
    open fun onClickViews() {}
    open fun observerData() {}


    fun navigateTo(
        clazz: Class<out AppCompatActivity>,
        bundle: Bundle? = null,
        navigationAnimation: Pair<Int, Int>? = Pair(R.anim.slide_in_right, R.anim.slide_out_left),
        isFinish: Boolean = false,
    ) {
        val intent = Intent(this, clazz)
        bundle?.let { intent.putExtras(it) }


        startActivity(
            intent,
            ActivityOptions.makeCustomAnimation(
                this,
                navigationAnimation?.first ?: 0,
                navigationAnimation?.second ?: 0
            ).toBundle()
        )

        if (isFinish) finish()
    }

    private fun navigateTo(
        clazz: Class<out AppCompatActivity>,
        bundle: Bundle? = null,
        navigationAnimation: Pair<Int, Int>? = Pair(R.anim.slide_in_right, R.anim.slide_out_left),
        intentFlag: NavigationFlag?
    ) {
        val intent = Intent(this, clazz)

        intentFlag?.let {
            when (it) {
                NavigationFlag.ClearTop -> intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK

                NavigationFlag.ClearTask -> intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

                NavigationFlag.NewTask -> intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        }

        bundle?.let { intent.putExtras(it) }


        startActivity(
            intent,
            ActivityOptions.makeCustomAnimation(
                this,
                navigationAnimation?.first ?: 0,
                navigationAnimation?.second ?: 0
            ).toBundle()
        )

    }

    fun navigateThenClearTask(
        clazz: Class<out AppCompatActivity>,
        bundle: Bundle? = null,
        navigationAnimation: Pair<Int, Int>? = Pair(R.anim.slide_in_right, R.anim.slide_out_left),
    ) {
        navigateTo(
            clazz,
            bundle,
            navigationAnimation,
            intentFlag = NavigationFlag.ClearTask
        )
    }

    fun navigateThenClearTop(
        clazz: Class<out AppCompatActivity>,
        bundle: Bundle? = null,
        navigationAnimation: Pair<Int, Int>? = Pair(R.anim.slide_in_right, R.anim.slide_out_left),

        ) {
        navigateTo(
            clazz,
            bundle,
            navigationAnimation,
            intentFlag = NavigationFlag.ClearTop
        )
    }

    fun navigateWithNewTask(
        clazz: Class<out AppCompatActivity>,
        bundle: Bundle? = null,
        navigationAnimation: Pair<Int, Int>? = Pair(R.anim.slide_in_right, R.anim.slide_out_left),
    ) {
        navigateTo(
            clazz,
            bundle,
            navigationAnimation,
            intentFlag = NavigationFlag.NewTask
        )
    }

    fun navigateBack(
        navigationAnimation: Pair<Int, Int> = Pair(R.anim.slide_in_right, R.anim.slide_out_left),
    ) {
        finish()

        overridePendingTransition(
            navigationAnimation.first,
            navigationAnimation.second
        )
    }

    fun navigateThenClearTask(
        clazz: Class<out BaseActivity<*>>,
        bundle: Bundle? = null,
        onCallback: (Intent) -> Unit = {}
    ) {
        val intent = Intent(this, clazz).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        bundle?.let { intent.putExtras(it) }
        onCallback.invoke(intent)
        startActivity(intent)
    }

    fun navigateForResult(
        clazz: Class<out AppCompatActivity>,
        bundle: Bundle? = null,
    ) {
        val intent = Intent(this, clazz)
        bundle?.let {
            intent.putExtras(bundle)
        }
        activityResultLauncher.launch(intent)
    }

    fun backFragment() {
        supportFragmentManager.popBackStack()
    }


    override fun attachBaseContext(newBase: Context?) {
        val ctx = newBase?.let { LanguageHelper.loadAndSetLocale(it) }
        super.attachBaseContext(ctx)
    }


    private fun setLayoutDirection() {
        binding.root.layoutDirection =
            if (isRTL()) View.LAYOUT_DIRECTION_RTL else View.LAYOUT_DIRECTION_LTR
    }

    override fun onResume() {
        window.hideSystemBar()
        super.onResume()
    }

    override fun onDestroy() {
        scope.close()
        _binding = null
        activityResultLauncher.unregister()
        super.onDestroy()
    }

}