package ru.dmitriyt.vkarchiver.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class UserAuthResponse(
    @SerialName("access_token") val accessToken: String?,
    @SerialName("user_id") val userId: Int?,
    @SerialName("expires_in") val expiresIn: Int?,
    @SerialName("email") val email: String?,
    @SerialName("error") val error: String?,
)