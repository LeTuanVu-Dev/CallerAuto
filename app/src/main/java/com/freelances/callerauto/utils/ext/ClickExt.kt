package com.freelances.callerauto.utils.ext

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.View

abstract class TapListener : View.OnClickListener {
    companion object {
        private const val DEBOUNCE = 400L
    }

    private var lastClickMillis: Long = 0
    private var now = 0L

    override fun onClick(v: View?) {
        now = SystemClock.elapsedRealtime()
        if (now - lastClickMillis > DEBOUNCE)
            onTap(v)
        lastClickMillis = now
    }

    abstract fun onTap(v: View?)
}

fun View.tap(action: (view: View?) -> Unit) {
    setOnClickListener(object : TapListener() {
        override fun onTap(v: View?) {
            action(v)
        }
    })
}
fun View.safeClick(debounceTime: Long = 500L, action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0
        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
            else action()
            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}

fun View.setOnSafeClickListener(debounceTime: Long = 300L, action: () -> Unit) {
    var lastClickTime = 0L
    val handler = Handler(Looper.getMainLooper())

    this.setOnClickListener {
        val currentTime = System.currentTimeMillis()

        if (currentTime - lastClickTime > debounceTime) {
            lastClickTime = currentTime
            handler.removeCallbacksAndMessages(null)
            handler.postDelayed({
                action.invoke()
            }, debounceTime)
        } else {
            handler.removeCallbacksAndMessages(null)
            handler.postDelayed({
                action.invoke()
            }, debounceTime)
        }
    }
}