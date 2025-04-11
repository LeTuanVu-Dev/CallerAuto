package com.freelances.callerauto.utils.helper

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

object CallCoordinator {
    private val _onCallFinished = MutableSharedFlow<Boolean>(replay = 0)
    val onCallFinished: SharedFlow<Boolean> get() = _onCallFinished

    suspend fun notifyCallFinished(value: Boolean) {
        _onCallFinished.emit(value)
    }
}
