package ru.dmitriyt.vkarchiver.domain

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.dmitriyt.vkarchiver.data.model.CachedWallPost
import ru.dmitriyt.vkarchiver.data.model.CachedWallPostAttachment
import ru.dmitriyt.vkarchiver.data.model.WallPost
import ru.dmitriyt.vkarchiver.data.model.WallPostAttachment
import ru.dmitriyt.vkarchiver.data.repository.CacheRepository
import ru.dmitriyt.vkarchiver.data.resources.Logger
import ru.dmitriyt.vkarchiver.data.resources.WallPostTemplates

class SaveWallPostsUseCase(
    private val cacheRepository: CacheRepository,
    private val gson: Gson,
    private val saveImageResourceToCacheUseCase: SaveImageResourceToCacheUseCase,
) {
    suspend operator fun invoke(
        directoryPath: String,
        domain: String,
        posts: List<WallPost>,
    ): Flow<Result> = flow {
        coroutineScope {
            Logger.d("SAVE_USE_CASE start")
            val startTime = System.currentTimeMillis()
            val cacheAsyncResourcePosts = posts.map { post ->
                val asyncAttachments = mutableListOf<Deferred<CachedWallPostAttachment?>>()
                val asyncImages = post.attachments
                    .filterIsInstance<WallPostAttachment.Photo>()
                    .mapIndexed { index, photo ->
                        async {
                            saveImageResourceToCacheUseCase(
                                uniqueId = "${post.fromId}_${post.id}_$index",
                                photo = photo,
                            )
                        }
                    }
                asyncAttachments.addAll(asyncImages)
                post to asyncAttachments
            }
            Logger.d("SAVE_USE_CASE start async tasks")
            val total = cacheAsyncResourcePosts.map { (_, attachments) -> attachments.size }.sumOf { it }
            var currentDownloaded = 0
            val cacheResourcePosts = cacheAsyncResourcePosts.map { (post, asyncAttachments) ->
                CachedWallPost(post, asyncAttachments.mapNotNull { imageRequest ->
                    val image = try {
                        imageRequest.await()
                    } catch (e: Exception) {
                        emit(Result.Error(e))
                        return@coroutineScope
                    }
                    currentDownloaded++
                    emit(Result.Progress(currentDownloaded.toFloat() / total))
                    image
                })
            }
            Logger.d("SAVE_USE_CASE end async tasks")
            val diffTime = System.currentTimeMillis() - startTime
            Logger.d("SAVE_IMAGES spent ${diffTime.toFloat() / 1000}s")
            val contentHtml = WallPostTemplates.getHtmlPosts(domain, cacheResourcePosts)
            val contentJson = gson.toJson(posts)
            val fileName = "group___$domain"
            cacheRepository.saveData(directoryPath, fileName, contentHtml, CacheRepository.Format.HTML)
            cacheRepository.saveData(directoryPath, fileName, contentJson, CacheRepository.Format.JSON)
            emit(Result.Data(fileName))
        }
    }

    sealed class Result {
        data class Progress(val progress: Float) : Result()
        data class Data(val filename: String) : Result()
        data class Error(val t: Throwable) : Result()
    }

    companion object {
        fun new() = SaveWallPostsUseCase(
            cacheRepository = CacheRepository(),
            gson = GsonBuilder().setPrettyPrinting().create(),
            saveImageResourceToCacheUseCase = SaveImageResourceToCacheUseCase.new(),
        )
    }
}