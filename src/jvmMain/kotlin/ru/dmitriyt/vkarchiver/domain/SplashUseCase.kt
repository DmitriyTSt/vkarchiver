package ru.dmitriyt.vkarchiver.domain

import ru.dmitriyt.vkarchiver.data.repository.AppEnvRepository

class SplashUseCase(
    private val appEnvRepository: AppEnvRepository,
    private val isAuthorizedUseCase: IsAuthorizedUseCase,
) {
    suspend operator fun invoke(): Result {
        val envValid = try {
            appEnvRepository.getEnv()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
        return if (!envValid) {
            Result.ERROR
        } else {
            if (isAuthorizedUseCase()) {
                Result.MAIN
            } else {
                Result.AUTH
            }
        }
    }

    enum class Result {
        ERROR,
        MAIN,
        AUTH,
    }

    companion object {
        fun new() = SplashUseCase(
            appEnvRepository = AppEnvRepository(),
            isAuthorizedUseCase = IsAuthorizedUseCase.new(),
        )
    }
}