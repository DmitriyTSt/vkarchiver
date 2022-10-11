package ru.dmitriyt.vkarchiver.data.repository

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import ru.dmitriyt.vkarchiver.data.model.AppEnv
import java.io.File

private const val APP_ENV_FILE_NAME = "env.json"

/**
 * Настройки приложения
 */
interface AppEnvRepository {
    suspend fun getEnv(): AppEnv
}

fun AppEnvRepository() = AppEnvRepositoryImpl()

class AppEnvRepositoryImpl : AppEnvRepository {

    override suspend fun getEnv(): AppEnv {
        return File(APP_ENV_FILE_NAME).readText().let { Json.decodeFromString(it) }
    }
}