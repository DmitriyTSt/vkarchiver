package ru.dmitriyt.vkarchiver.presentation.ui.savewall

import java.time.LocalDateTime

sealed class SaveWallState {
    object Idle : SaveWallState()
    data class Loading(val progress: Float) : SaveWallState()
    data class Error(val message: String) : SaveWallState()
    data class Success(val domain: String, val time: LocalDateTime) : SaveWallState()
}