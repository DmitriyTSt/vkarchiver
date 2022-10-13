package ru.dmitriyt.vkarchiver.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.dmitriyt.vkarchiver.data.extensions.orDefault
import ru.dmitriyt.vkarchiver.data.extensions.orThrow
import ru.dmitriyt.vkarchiver.data.mapper.WallPostMapper
import ru.dmitriyt.vkarchiver.data.model.AuthResult
import ru.dmitriyt.vkarchiver.data.model.ListData
import ru.dmitriyt.vkarchiver.data.model.UserActor
import ru.dmitriyt.vkarchiver.data.model.WallPost
import ru.dmitriyt.vkarchiver.data.source.VkApiService

interface VkApiRepository {
    suspend fun authByCode(code: String): AuthResult
    suspend fun loadWallPosts(userActor: UserActor, domain: String, offset: Int, limit: Int): ListData<WallPost>
}

fun VkApiRepository(): VkApiRepository = VkApiRepositoryImpl(VkApiService(), AppEnvRepository(), WallPostMapper())

private class VkApiRepositoryImpl(
    private val vkApiService: VkApiService,
    private val appEnvRepository: AppEnvRepository,
    private val mapper: WallPostMapper,
) : VkApiRepository {

    override suspend fun authByCode(code: String): AuthResult = withContext(Dispatchers.IO) {
        val appEnv = appEnvRepository.getEnv()
        val userAuthResponse = vkApiService.oAuth()
            .userAuthorizationCodeFlow(appEnv.appId, appEnv.clientSecret, appEnv.redirectUri, code)

        AuthResult(
            userAuthResponse.userId.orThrow("userId is null"),
            userAuthResponse.accessToken.orThrow("accessToken is null"),
        )
    }

    override suspend fun loadWallPosts(
        userActor: UserActor,
        domain: String,
        offset: Int,
        limit: Int,
    ): ListData<WallPost> = withContext(Dispatchers.IO) {
        val response = vkApiService.api()
            .wallPosts(userActor.accessToken.orThrow("access token is null"), domain, offset, limit)
            .response
        val data = ListData(response.count.orDefault(), response.items.orEmpty().map { mapper.fromApiToModel(it) })
        return@withContext data
    }
}