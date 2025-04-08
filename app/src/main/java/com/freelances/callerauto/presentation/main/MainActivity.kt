package com.freelances.callerauto.presentation.main

import com.freelances.callerauto.databinding.ActivityMainBinding
import com.freelances.callerauto.presentation.bases.BaseActivity
import com.freelances.callerauto.presentation.setting.SettingActivity
import com.freelances.callerauto.utils.ext.safeClick

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    override fun initViews() {
        initData()
        initAction()
    }

    private fun initData() {
        sharedPreference.isDoneFirstOpen = true
    }
    private fun initAction() {
        binding.apply {
            buttonSetting.safeClick {
                navigateTo(SettingActivity::class.java)
            }
        }
    }
}