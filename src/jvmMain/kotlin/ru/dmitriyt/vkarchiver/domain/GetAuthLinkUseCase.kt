package ru.dmitriyt.vkarchiver.domain

import ru.dmitriyt.vkarchiver.data.repository.AppEnvRepository

class GetAuthLinkUseCase(
    private val appEnvRepository: AppEnvRepository,
) {
    suspend operator fun invoke(): String {
        val env = appEnvRepository.getEnv()
        return "https://oauth.vk.com/authorize?client_id=${env.appId}&redirect_uri=${env.redirectUri}"
    }

    companion object {
        fun new() = GetAuthLinkUseCase(AppEnvRepository())
    }
}