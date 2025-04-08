package com.freelances.callerauto.model

import android.os.Parcelable
import com.freelances.callerauto.utils.ext.generateUniqueId
import kotlinx.parcelize.Parcelize

@Parcelize
data class LanguageModel(
    val id: String = generateUniqueId(),
    val localeName: String,
    val internationalName: String,
    val iso: String,
    val flag: Int,
    val isSelected: Boolean = false
) : Parcelable