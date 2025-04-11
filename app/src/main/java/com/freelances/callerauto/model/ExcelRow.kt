package com.freelances.callerauto.model

import com.freelances.callerauto.di.Carrier

data class ExcelRow(
    val name: String?,
    val phoneNumber: String?,
    val nickName: String?,
    val type:Carrier = Carrier.UNKNOWN,
    val selected: Boolean = false
)
