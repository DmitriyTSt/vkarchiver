package ru.dmitriyt.vkarchiver.domain

import ru.dmitriyt.vkarchiver.data.repository.SettingsRepository

class IsAuthorizedUseCase(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(): Boolean {
        return settingsRepository.getSettings().let { it.userId != null && it.accessToken != null }
    }

    companion object {
        fun new() = IsAuthorizedUseCase(SettingsRepository())
    }
}