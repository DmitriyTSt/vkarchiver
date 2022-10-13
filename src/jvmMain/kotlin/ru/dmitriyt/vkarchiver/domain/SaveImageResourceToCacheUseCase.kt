package ru.dmitriyt.vkarchiver.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.dmitriyt.vkarchiver.data.model.CachedWallPostAttachment
import ru.dmitriyt.vkarchiver.data.model.WallPostAttachment
import ru.dmitriyt.vkarchiver.data.repository.ResourceCacheRepository
import ru.dmitriyt.vkarchiver.data.resources.Logger
import java.io.File

private const val CACHE_DIRECTORY_NAME = "cache_images"

class SaveImageResourceToCacheUseCase(
    private val getCacheDirectoryUseCase: GetCacheDirectoryUseCase,
    private val repository: ResourceCacheRepository,
) {

    suspend operator fun invoke(
        postFromId: Int,
        postId: Int,
        index: Int,
        photo: WallPostAttachment.Photo,
    ): CachedWallPostAttachment.Image? {
        val resourceId = "image${postFromId}_${postId}_$index"
        val imageCachePath = "${getCacheDirectoryUseCase()}${File.separator}$CACHE_DIRECTORY_NAME"
        val imageCacheDir = File(imageCachePath)
        withContext(Dispatchers.IO) {
            if (!imageCacheDir.exists()) {
                imageCacheDir.mkdir()
            }
        }
        val filePath = try {
            repository.saveResourceToCache(resourceId, photo.url, imageCachePath, "jpg")
        } catch (e: Exception) {
            Logger.e(e)
            return null
        }
        return CachedWallPostAttachment.Image(filePath)
    }

    companion object {
        fun new() = SaveImageResourceToCacheUseCase(
            getCacheDirectoryUseCase = GetCacheDirectoryUseCase.new(),
            repository = ResourceCacheRepository(),
        )
    }
}