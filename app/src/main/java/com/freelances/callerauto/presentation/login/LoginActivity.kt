package com.freelances.callerauto.presentation.login

import android.widget.Toast
import com.freelances.callerauto.databinding.ActivityLoginBinding
import com.freelances.callerauto.presentation.bases.BaseActivity
import com.freelances.callerauto.presentation.main.MainActivity
import com.freelances.callerauto.utils.ext.hideKeyboard
import com.freelances.callerauto.utils.ext.tap
import com.freelances.callerauto.utils.helper.DeviceKeyManager

class LoginActivity : BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {

    override fun initViews() {
        if (sharedPreference.valueLogin.isNotEmpty()) {
            navigateTo(MainActivity::class.java, isFinish = true)
        }
        binding.tvApply.tap {
            hideKeyboard()
            val input = binding.edInput.text.toString().trim()
            DeviceKeyManager.getOrValidateDeviceKey(this, input) { success, key ->
                if (success) {
                    sharedPreference.valueLogin = input
                    Toast.makeText(this, "Đăng nhập thành công với key: $key", Toast.LENGTH_SHORT)
                        .show()
                    // Cho truy cập app
                    navigateTo(MainActivity::class.java, isFinish = true)
                } else {
                    Toast.makeText(this, "Sai key hoặc chưa có quyền truy cập", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}