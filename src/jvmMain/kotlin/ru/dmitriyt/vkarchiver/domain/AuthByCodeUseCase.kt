package ru.dmitriyt.vkarchiver.domain

import com.vk.api.sdk.client.actors.UserActor
import ru.dmitriyt.vkarchiver.data.repository.SettingsRepository
import ru.dmitriyt.vkarchiver.data.repository.VkApiRepository

class AuthByCodeUseCase(
    private val settingsRepository: SettingsRepository,
    private val vkApiRepository: VkApiRepository,
) {
    suspend operator fun invoke(code: String): UserActor {
        val authResult = vkApiRepository.authByCode(code)
        settingsRepository.saveSettings(
            settingsRepository.getSettings().copy(
                userId = authResult.userId,
                accessToken = authResult.accessToken,
            )
        )
        return UserActor(authResult.userId, authResult.accessToken)
    }

    companion object {
        val instance = AuthByCodeUseCase(
            settingsRepository = SettingsRepository(),
            vkApiRepository = VkApiRepository(),
        )
        fun new() = AuthByCodeUseCase(
            settingsRepository = SettingsRepository(),
            vkApiRepository = VkApiRepository(),
        )
    }
}