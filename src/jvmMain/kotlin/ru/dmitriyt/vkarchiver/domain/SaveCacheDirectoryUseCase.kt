package ru.dmitriyt.vkarchiver.domain

import ru.dmitriyt.vkarchiver.data.repository.SettingsRepository

class SaveCacheDirectoryUseCase(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(directoryPath: String) {
        settingsRepository.saveSettings(
            settingsRepository.getSettings().copy(cacheDirectoryPath = directoryPath)
        )
    }

    companion object {
        val instance = SaveCacheDirectoryUseCase(SettingsRepository())
    }
}