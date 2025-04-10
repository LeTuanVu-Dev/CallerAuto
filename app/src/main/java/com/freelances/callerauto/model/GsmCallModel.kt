package com.freelances.callerauto.model

class GsmCallModel(val status: Status, val displayName: String?, val phoneNumber:String?, val nickName:String?) {

    enum class Status {
        CONNECTING,
        DIALING,
        RINGING,
        ACTIVE,
        DISCONNECTED,
        UNKNOWN
    }
}