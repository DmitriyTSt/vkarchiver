package ru.dmitriyt.vkarchiver.domain

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.vk.api.sdk.objects.wall.WallpostFull
import ru.dmitriyt.vkarchiver.data.repository.CacheRepository
import ru.dmitriyt.vkarchiver.data.resources.WallPostTemplates

class SaveWallPostsUseCase(
    private val cacheRepository: CacheRepository,
    private val gson: Gson,
) {
    suspend operator fun invoke(directoryPath: String, domain: String, posts: List<WallpostFull>): String {
        val contentHtml = WallPostTemplates.getHtmlPosts(domain, posts)
        val contentJson = gson.toJson(posts)
        val fileName = "group___$domain"
        cacheRepository.saveData(directoryPath, fileName, contentHtml, CacheRepository.Format.HTML)
        cacheRepository.saveData(directoryPath, fileName, contentJson, CacheRepository.Format.JSON)
        return fileName
    }

    companion object {
        val instance = SaveWallPostsUseCase(
            cacheRepository = CacheRepository(),
            gson = GsonBuilder().setPrettyPrinting().create(),
        )

        fun new() = SaveWallPostsUseCase(
            cacheRepository = CacheRepository(),
            gson = GsonBuilder().setPrettyPrinting().create(),
        )
    }
}