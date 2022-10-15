package ru.dmitriyt.vkarchiver.data.source

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import ru.dmitriyt.vkarchiver.data.remote.response.BaseResponse
import ru.dmitriyt.vkarchiver.data.remote.response.UserAuthResponse
import ru.dmitriyt.vkarchiver.data.remote.response.WallPostsResponse

private const val API_DOMAIN = "api.vk.com"
private const val API_PATH = "method/"
private const val OAUTH_DOMAIN = "oauth.vk.com"
private const val API_VERSION = "5.131"

interface VkApiService {
    fun oAuth(): OAuth
    fun api(): Api

    interface OAuth {
        suspend fun userAuthorizationCodeFlow(
            clientId: Int,
            clientSecret: String,
            redirectUri: String,
            code: String,
        ): UserAuthResponse
    }

    interface Api {
        suspend fun wallPosts(accessToken: String, domain: String, offset: Int, limit: Int): BaseResponse<WallPostsResponse>
    }
}

fun VkApiService(): VkApiService = VkApiServiceImpl(OAuth(), Api())

private class VkApiServiceImpl(
    private val oAuth: VkApiService.OAuth,
    private val api: VkApiService.Api,
) : VkApiService {
    override fun oAuth(): VkApiService.OAuth {
        return oAuth
    }

    override fun api(): VkApiService.Api {
        return api
    }
}

private fun OAuth(): VkApiService.OAuth = OAuthImpl()

private class OAuthImpl : VkApiService.OAuth {
    @OptIn(ExperimentalSerializationApi::class)
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    encodeDefaults = false
                    explicitNulls = false
                }
            )
        }
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.BODY
        }
        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = OAUTH_DOMAIN
                parameters.append("v", API_VERSION)
            }
        }
    }

    override suspend fun userAuthorizationCodeFlow(
        clientId: Int,
        clientSecret: String,
        redirectUri: String,
        code: String,
    ): UserAuthResponse {
        return client.get("access_token") {
            parameter("client_id", clientId)
            parameter("redirect_uri", redirectUri)
            parameter("client_secret", clientSecret)
            parameter("code", code)
        }.body()
    }
}

private fun Api(): VkApiService.Api = ApiImpl()

private class ApiImpl : VkApiService.Api {
    @OptIn(ExperimentalSerializationApi::class)
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    encodeDefaults = false
                    explicitNulls = false
                }
            )
        }
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.BODY
        }
        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = API_DOMAIN
                path(API_PATH)
                parameters.append("v", API_VERSION)
            }
        }
    }

    override suspend fun wallPosts(
        accessToken: String,
        domain: String,
        offset: Int,
        limit: Int
    ): BaseResponse<WallPostsResponse> {
        return client.get("wall.get") {
            parameter("access_token", accessToken)
            parameter("domain", domain)
            parameter("offset", offset)
            parameter("count", limit)
        }.body()
    }
}



