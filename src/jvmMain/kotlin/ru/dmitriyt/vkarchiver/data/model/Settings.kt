package ru.dmitriyt.vkarchiver.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    val userId: Int? = null,
    val accessToken: String? = null,
)