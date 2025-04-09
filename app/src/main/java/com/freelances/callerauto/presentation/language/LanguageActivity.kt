package com.freelances.callerauto.presentation.language

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.freelances.callerauto.databinding.ActivityLanguageBinding
import com.freelances.callerauto.model.LanguageModel
import com.freelances.callerauto.presentation.adapters.LanguageAdapter
import com.freelances.callerauto.presentation.bases.BaseActivity
import com.freelances.callerauto.presentation.main.MainActivity
import com.freelances.callerauto.presentation.permission.PermissionActivity
import com.freelances.callerauto.utils.ext.invisible
import com.freelances.callerauto.utils.ext.safeClick
import com.freelances.callerauto.utils.ext.visible
import com.freelances.callerauto.utils.helper.LanguageHelper.firstOpenLanguages

open class LanguageActivity :
    BaseActivity<ActivityLanguageBinding>(ActivityLanguageBinding::inflate) {
    companion object {
        var scrollOffsetY: Int = 0
        var selectedPosition: Int = -1
    }

    private val languageAdapter: LanguageAdapter by lazy {
        LanguageAdapter(::onItemLanguageClicked)
    }

    open val listData: List<LanguageModel> = firstOpenLanguages

    override fun initViews() {
        if (resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
            binding.buttonBack.scaleX = -1f // Lật ngược khi RTL
        } else {
            binding.buttonBack.scaleX = 1f // Giữ nguyên khi LTR
        }
        binding.buttonDone.invisible()
        setUpAdapter()
        binding.buttonBack.safeClick {
            finish()
        }
        binding.buttonDone.safeClick {
            val selectedLanguageCode = languageAdapter.currentList.find { it.isSelected }?.iso
            val selectedLanguageName =
                languageAdapter.currentList.find { it.isSelected }?.localeName
            if (selectedLanguageCode != null) {
                sharedPreference.languageCode = selectedLanguageCode
                sharedPreference.languageName = selectedLanguageName ?: ""
            }
            navigateToNextScreen()
        }
    }


    private fun navigateToNextScreen() {
        navigateThenClearTask(PermissionActivity::class.java)
    }

    private fun setUpAdapter() {
        binding.rcvLanguage.apply {
            adapter = languageAdapter
            layoutManager = LinearLayoutManager(this@LanguageActivity).also {
                it.scrollToPositionWithOffset(0, -1 * scrollOffsetY)
            }
            languageAdapter.submitList(listData)
            languageAdapter.setSelectedLanguage(sharedPreference.languageCode)
        }
    }

    open fun onItemLanguageClicked(position: Int) {
        selectedPosition = position
        languageAdapter.setSelectedLanguage(position)
        binding.buttonDone.visible()
    }

    override fun isDisplayCutout(): Boolean = true

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}