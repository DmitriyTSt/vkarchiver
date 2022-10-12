package ru.dmitriyt.vkarchiver.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL

interface ResourceCacheRepository {
    suspend fun saveResourceToCache(id: String, url: String, cacheDirectoryPath: String, extension: String): String
}

fun ResourceCacheRepository(): ResourceCacheRepository = ResourceCacheRepositoryImpl()

class ResourceCacheRepositoryImpl : ResourceCacheRepository {
    override suspend fun saveResourceToCache(id: String, url: String, cacheDirectoryPath: String, extension: String): String {
        val outputFilePath = buildString {
            append(cacheDirectoryPath)
            append(File.separator)
            append(id)
            append(".")
            append(extension)
        }
        downloadFile(url, outputFilePath)
        return outputFilePath
    }

    private suspend fun downloadFile(url: String, outputFilePath: String) = withContext(Dispatchers.IO) {
        URL(url).openStream().use { inp ->
            BufferedInputStream(inp).use { bis ->
                FileOutputStream(outputFilePath).use { fos ->
                    val data = ByteArray(2048)
                    var count: Int
                    while (bis.read(data, 0, 2048).also { count = it } != -1) {
                        fos.write(data, 0, count)
                    }
                }
            }
        }
    }
}