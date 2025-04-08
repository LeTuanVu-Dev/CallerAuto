package com.freelances.callerauto.utils.helper

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import com.freelances.callerauto.R
import com.freelances.callerauto.model.LanguageModel
import com.freelances.callerauto.utils.preference.AppSharedPreference
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.Locale

object LanguageHelper : KoinComponent {

    private val appSharedPreference: AppSharedPreference by inject<AppSharedPreference>()
    fun onAttach(context: Context, defaultLanguage: String): Context? {
        return setLocale(context, defaultLanguage)
    }

    fun setLocale(context: Context, language: String): Context? {
        return updateConfiguration(context, language)
    }

    fun loadAndSetLocale(context: Context): Context? {
        var language: String = appSharedPreference.languageCode
        if (language == "") {
            language = Locale.getDefault().language
        }
        return updateConfiguration(context, language)
    }

    private fun getDeviceLanguageCode(): String {
        return Resources.getSystem().configuration.locales[0].language
    }

    private fun updateConfiguration(context: Context, language: String): Context? {
        if (language.equals("", ignoreCase = true)) return context
        var locale = Locale(language)
        if (language.contains("_")) {
            locale =
                Locale(
                    language.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0],
                    language.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1],
                )
        }
        Locale.setDefault(locale)
        val configuration: Configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        return context.createConfigurationContext(configuration)
    }

    fun getLanguageName(languageCode: String): String {
        return settingLanguages.find { it.iso == languageCode }?.localeName ?: ""
    }

    private val settingLanguages = mutableListOf(
        LanguageModel(
            localeName = "English",
            internationalName = "English",
            iso = "en",
            flag = R.drawable.ic_flag_uk,
            isSelected = false
        ),

        LanguageModel(
            localeName = "Tiếng Việt",
            internationalName = "Vietnamese",
            iso = "vi",
            flag = R.drawable.ic_flag_vietnam,
            isSelected = false
        ),
    )

    val firstOpenLanguages = settingLanguages

    fun preUpdateListLanguage(): List<LanguageModel> {
        return firstOpenLanguages
    }
}
