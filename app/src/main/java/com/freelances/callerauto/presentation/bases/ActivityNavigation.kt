package com.freelances.callerauto.presentation.bases

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Pair
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.freelances.callerauto.R

enum class NavigationFlag {
    ClearTask,
    ClearTop,
    NewTask
}


interface ActivityNavigation {

    fun navigateTo(
        clazz: Class<out AppCompatActivity>,
        bundle: Bundle? = null,
        navigationAnimation: Pair<Int, Int>? = Pair(R.anim.slide_in_right, R.anim.slide_out_left),
        isFinish: Boolean = false,
    )

    fun navigateTo(
        clazz: Class<out AppCompatActivity>,
        bundle: Bundle? = null,
        navigationAnimation: Pair<Int, Int>? = Pair(R.anim.slide_in_right, R.anim.slide_out_left),
        intentFlag: NavigationFlag? = null
    )

    fun navigateBack()

    fun navigateForResult(
        clazz: Class<out AppCompatActivity>,
        bundle: Bundle? = null,
        navigationAnimation: Pair<Int, Int>? = Pair(R.anim.slide_in_right, R.anim.slide_out_left),
        isFinish: Boolean = false,
    )
    fun navigateForResult(
        clazz: Class<out AppCompatActivity>,
        bundle: Bundle? = null,
        navigationAnimation: Pair<Int, Int>? = Pair(R.anim.slide_in_right, R.anim.slide_out_left),
        launcher : ActivityResultLauncher<Intent>
    )

    fun setResultBack(key : Int = Activity.RESULT_OK, bundle: Bundle? = null)

}

interface MockActivityNavigation : ActivityNavigation {
    override fun navigateTo(
        clazz: Class<out AppCompatActivity>,
        bundle: Bundle?,
        navigationAnimation: Pair<Int, Int>?,
        isFinish: Boolean,
    ) {
    }

    override fun navigateTo(
        clazz: Class<out AppCompatActivity>,
        bundle: Bundle?,
        navigationAnimation: Pair<Int, Int>?,
        intentFlag: NavigationFlag?
    ) {
    }

    override fun navigateBack() {}
    override fun setResultBack(key: Int, bundle: Bundle?) {}
    override fun navigateForResult(
        clazz: Class<out AppCompatActivity>,
        bundle: Bundle?,
        navigationAnimation: Pair<Int, Int>?,
        isFinish: Boolean,
    ) {
    }

    override fun navigateForResult(
        clazz: Class<out AppCompatActivity>,
        bundle: Bundle?,
        navigationAnimation: Pair<Int, Int>?,
        launcher : ActivityResultLauncher<Intent>
    ) {
    }
}