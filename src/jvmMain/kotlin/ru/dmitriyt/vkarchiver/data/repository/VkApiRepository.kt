package ru.dmitriyt.vkarchiver.data.repository

import com.vk.api.sdk.client.VkApiClient
import com.vk.api.sdk.client.actors.UserActor
import com.vk.api.sdk.httpclient.HttpTransportClient
import com.vk.api.sdk.objects.wall.responses.GetResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.dmitriyt.vkarchiver.data.extensions.orThrow
import ru.dmitriyt.vkarchiver.data.model.AuthResult

interface VkApiRepository {
    suspend fun authByCode(code: String): AuthResult
    suspend fun loadWallPosts(userActor: UserActor, domain: String, offset: Int, limit: Int): GetResponse
}

fun VkApiRepository() = VkApiRepositoryImpl(AppEnvRepository())

class VkApiRepositoryImpl(
    private val appEnvRepository: AppEnvRepository,
) : VkApiRepository {
    private val vk = VkApiClient(HttpTransportClient.getInstance())

    override suspend fun authByCode(code: String): AuthResult = withContext(Dispatchers.IO) {
        val appEnv = appEnvRepository.getEnv()
        val userAuthResponse = vk.oAuth()
            .userAuthorizationCodeFlow(appEnv.appId, appEnv.clientSecret, appEnv.redirectUri, code)
            .execute()

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
    ): GetResponse = withContext(Dispatchers.IO) {
        vk.wall().get(userActor)
            .domain(domain)
            .offset(offset)
            .count(limit)
            .execute()
    }
}