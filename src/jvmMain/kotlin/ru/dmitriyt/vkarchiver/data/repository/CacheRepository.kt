package ru.dmitriyt.vkarchiver.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Работа с архивными данными
 */
interface CacheRepository {
    suspend fun saveData(directoryPath: String, filename: String, content: String, format: Format)

    enum class Format(val ext: String) {
        JSON("json"),
        HTML("html"),
    }
}

fun CacheRepository(): CacheRepository = CacheRepositoryImpl()

private class CacheRepositoryImpl : CacheRepository {
    override suspend fun saveData(
        directoryPath: String,
        filename: String,
        content: String,
        format: CacheRepository.Format,
    ) = withContext(Dispatchers.IO) {
        val directory = File(directoryPath)
        if (!directory.exists()) {
            directory.mkdir()
        }
        val newJsonFile = File(directory, "$filename.${format.ext}")
        if (!newJsonFile.exists()) {
            newJsonFile.createNewFile()
        }

        newJsonFile.writeText(content)
    }
}