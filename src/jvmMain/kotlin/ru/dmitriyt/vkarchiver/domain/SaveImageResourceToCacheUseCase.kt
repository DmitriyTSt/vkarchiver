package ru.dmitriyt.vkarchiver.domain

import com.vk.api.sdk.objects.photos.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.dmitriyt.vkarchiver.data.model.PostAttachment
import ru.dmitriyt.vkarchiver.data.repository.ResourceCacheRepository
import ru.dmitriyt.vkarchiver.data.resources.Logger
import java.io.File

private const val CACHE_DIRECTORY_NAME = "cache_images"

class SaveImageResourceToCacheUseCase(
    private val getCacheDirectoryUseCase: GetCacheDirectoryUseCase,
    private val repository: ResourceCacheRepository,
) {

    suspend operator fun invoke(postFromId: Int, postId: Int, index: Int, photo: Photo): PostAttachment.Image? {
        val resourceId = "image${postFromId}_${postId}_$index"
        val url = photo.sizes.maxByOrNull { it.width }?.url?.toString() ?: return null
        val imageCachePath = "${getCacheDirectoryUseCase()}${File.separator}$CACHE_DIRECTORY_NAME"
        val imageCacheDir = File(imageCachePath)
        withContext(Dispatchers.IO) {
            if (!imageCacheDir.exists()) {
                imageCacheDir.mkdir()
            }
        }
        val filePath = try {
            repository.saveResourceToCache(resourceId, url, imageCachePath, "jpg")
        } catch (e: Exception) {
            Logger.e(e)
            return null
        }
        return PostAttachment.Image(filePath)
    }

    companion object {
        fun new() = SaveImageResourceToCacheUseCase(
            getCacheDirectoryUseCase = GetCacheDirectoryUseCase.new(),
            repository = ResourceCacheRepository(),
        )
    }
}