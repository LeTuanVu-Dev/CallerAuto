package com.freelances.callerauto.model

import com.freelances.callerauto.di.Carrier
import com.freelances.callerauto.utils.ext.generateUniqueId

data class ExcelRow(
    val id:String? = generateUniqueId(),
    val name: String?,
    val phoneNumber: String?,
    val nickName: String?,
    val type:Carrier = Carrier.UNKNOWN,
    val selected: Boolean = false
)
