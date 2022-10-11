package ru.dmitriyt.vkarchiver.domain

import com.vk.api.sdk.objects.wall.WallpostFull
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.dmitriyt.vkarchiver.data.repository.CacheRepository
import ru.dmitriyt.vkarchiver.data.resources.WallPostTemplates

class SaveWallPostsUseCase(
    private val cacheRepository: CacheRepository,
) {
    suspend operator fun invoke(directoryPath: String, domain: String, posts: List<WallpostFull>): String {
        val contentHtml = WallPostTemplates.getHtmlPosts(domain, posts)
        val contentJson = Json.encodeToString(posts)
        val fileName = "group___$domain"
        cacheRepository.saveData(directoryPath, fileName, contentHtml, CacheRepository.Format.HTML)
        cacheRepository.saveData(directoryPath, fileName, contentJson, CacheRepository.Format.JSON)
        return fileName
    }

    companion object {
        val instance = SaveWallPostsUseCase(
            cacheRepository = CacheRepository(),
        )
    }
}