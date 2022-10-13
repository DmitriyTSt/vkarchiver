package ru.dmitriyt.vkarchiver.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
class ApiPhotoSizes(
    val url: String?,
    val width: Int?,
    val height: Int?,
)