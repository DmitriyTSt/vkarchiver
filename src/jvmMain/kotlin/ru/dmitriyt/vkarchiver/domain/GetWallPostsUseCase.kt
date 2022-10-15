package ru.dmitriyt.vkarchiver.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.dmitriyt.vkarchiver.data.model.UserActor
import ru.dmitriyt.vkarchiver.data.model.WallPost
import ru.dmitriyt.vkarchiver.data.repository.SettingsRepository
import ru.dmitriyt.vkarchiver.data.repository.VkApiRepository
import ru.dmitriyt.vkarchiver.data.resources.Logger

private const val DEFAULT_LIMIT = 50

class GetWallPostsUseCase(
    private val settingsRepository: SettingsRepository,
    private val vkApiRepository: VkApiRepository,
) {
    suspend operator fun invoke(domain: String): Flow<Result> = flow {
        val actor = settingsRepository.getSettings().let { UserActor(it.userId, it.accessToken) }
        val firstPartResponse = try {
            vkApiRepository.loadWallPosts(
                userActor = actor,
                domain = domain,
                offset = 0,
                limit = DEFAULT_LIMIT,
            )
        } catch (e: Exception) {
            Logger.e(e)
            emit(Result.Error(e))
            return@flow
        }

        val total = firstPartResponse.count
        val allPosts = mutableListOf<WallPost>()
        allPosts.addAll(firstPartResponse.items)

        emit(Result.Progress(allPosts.size.toFloat() / total))

        repeat(total / DEFAULT_LIMIT) {
            val partResponse = try {
                vkApiRepository.loadWallPosts(
                    userActor = actor,
                    domain = domain,
                    offset = DEFAULT_LIMIT + DEFAULT_LIMIT * it,
                    limit = DEFAULT_LIMIT,
                )
            } catch (e: Exception) {
                Logger.e(e)
                emit(Result.Error(e))
                return@flow
            }
            allPosts.addAll(partResponse.items)
            emit(Result.Progress(allPosts.size.toFloat() / total))
        }

        emit(Result.Data(allPosts))
    }

    sealed class Result {
        data class Progress(val progress: Float) : Result()
        data class Data(val items: List<WallPost>) : Result()
        data class Error(val t: Throwable) : Result()
    }

    companion object {
        fun new() = GetWallPostsUseCase(
            settingsRepository = SettingsRepository(),
            vkApiRepository = VkApiRepository(),
        )
    }
}