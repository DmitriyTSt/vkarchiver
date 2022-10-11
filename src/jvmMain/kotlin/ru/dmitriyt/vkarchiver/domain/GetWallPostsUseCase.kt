package ru.dmitriyt.vkarchiver.domain

import com.vk.api.sdk.client.actors.UserActor
import com.vk.api.sdk.objects.wall.WallpostFull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.dmitriyt.vkarchiver.data.repository.SettingsRepository
import ru.dmitriyt.vkarchiver.data.repository.VkApiRepository

private const val DEFAULT_LIMIT = 50

class GetWallPostsUseCase(
    private val settingsRepository: SettingsRepository,
    private val vkApiRepository: VkApiRepository,
) {
    suspend operator fun invoke(domain: String): Flow<Result> = flow {
        val actor = settingsRepository.getSettings().let { UserActor(it.userId, it.accessToken) }
        val firstPartResponse = vkApiRepository.loadWallPosts(
            userActor = actor,
            domain = domain,
            offset = 0,
            limit = DEFAULT_LIMIT,
        )

        val total = firstPartResponse.count
        val allPosts = mutableListOf<WallpostFull>()
        allPosts.addAll(firstPartResponse.items)

        emit(Result.Progress(allPosts.size.toFloat() / total))

        repeat(total / DEFAULT_LIMIT) {
            val partResponse = vkApiRepository.loadWallPosts(
                userActor = actor,
                domain = domain,
                offset = DEFAULT_LIMIT + DEFAULT_LIMIT * it,
                limit = DEFAULT_LIMIT,
            )

            allPosts.addAll(partResponse.items)
            emit(Result.Progress(allPosts.size.toFloat() / total))
        }

        emit(Result.Data(allPosts))
    }

    sealed class Result {
        data class Progress(val progress: Float) : Result()
        data class Data(val items: List<WallpostFull>) : Result()
    }

    companion object {
        val instance = GetWallPostsUseCase(
            settingsRepository = SettingsRepository(),
            vkApiRepository = VkApiRepository(),
        )
    }
}