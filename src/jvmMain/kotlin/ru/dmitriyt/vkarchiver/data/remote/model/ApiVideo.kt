package ru.dmitriyt.vkarchiver.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
class ApiVideo(
    val id: Int?,
    val ownerId: Int?,
)