package ru.dmitriyt.vkarchiver.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
class ApiPhoto(
    val sizes: List<ApiPhotoSizes>?,
)