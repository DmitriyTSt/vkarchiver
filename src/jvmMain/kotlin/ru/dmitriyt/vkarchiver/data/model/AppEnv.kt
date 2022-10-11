package ru.dmitriyt.vkarchiver.data.model

import kotlinx.serialization.Serializable

@Serializable
class AppEnv(
    val appId: Int,
    val clientSecret: String,
    val redirectUri: String,
)