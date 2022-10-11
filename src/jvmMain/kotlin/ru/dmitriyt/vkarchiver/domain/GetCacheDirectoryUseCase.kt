package ru.dmitriyt.vkarchiver.domain

import ru.dmitriyt.vkarchiver.data.repository.SettingsRepository

class GetCacheDirectoryUseCase(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(): String? {
        return settingsRepository.getSettings().cacheDirectoryPath
    }

    companion object {
        fun new() = GetCacheDirectoryUseCase(SettingsRepository())
    }
}