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
        val saved = sharedPreference.valueLogin
        if (saved.isNotEmpty()) {
            DeviceKeyManager.verifySavedKey(this, saved) { success, msg ->
                if (success) {
                    navigateTo(MainActivity::class.java, isFinish = true)
                } else {
                    Toast.makeText(this, msg ?: "Vui lòng nhập key", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.tvApply.tap {
            hideKeyboard()
            val input = binding.edInput.text.toString().trim()
            if (input.isEmpty()) {
                Toast.makeText(this, "Nhập key trước khi xác nhận", Toast.LENGTH_SHORT).show()
                return@tap
            }

            DeviceKeyManager.validateAndClaimKey(this, input) { success, msg ->
                if (success) {
                    sharedPreference.valueLogin = input.uppercase()
                    Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                    navigateTo(MainActivity::class.java, isFinish = true)
                } else {
                    Toast.makeText(this, msg ?: "Sai key hoặc chưa có quyền", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}