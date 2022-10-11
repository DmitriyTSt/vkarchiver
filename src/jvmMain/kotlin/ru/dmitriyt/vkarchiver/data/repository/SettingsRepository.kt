package ru.dmitriyt.vkarchiver.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.dmitriyt.vkarchiver.data.model.Settings
import java.io.File
import java.io.IOException

private const val SETTINGS_FILE_NAME = "settings.json"

/**
 * Пользовательские настройки и данные
 */
interface SettingsRepository {
    suspend fun getSettings(): Settings
    suspend fun getDefaultSettings(): Settings
    suspend fun saveSettings(settings: Settings)
}

fun SettingsRepository() = SettingsRepositoryImpl()

class SettingsRepositoryImpl : SettingsRepository {
    override suspend fun getSettings(): Settings = withContext(Dispatchers.IO) {
        try {
            File(SETTINGS_FILE_NAME).readText().let { Json.decodeFromString(it) }
        } catch (e: IOException) {
            getDefaultSettings()
        }
    }

    override suspend fun getDefaultSettings(): Settings {
        return Settings()
    }

    override suspend fun saveSettings(settings: Settings): Unit = withContext(Dispatchers.IO) {
        File(SETTINGS_FILE_NAME).apply {
            if (!exists()) {
                createNewFile()
            }
            writeText(Json.encodeToString(settings))
        }
    }
}