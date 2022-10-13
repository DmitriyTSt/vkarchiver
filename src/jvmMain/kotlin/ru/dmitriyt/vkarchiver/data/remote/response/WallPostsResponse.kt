package ru.dmitriyt.vkarchiver.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.dmitriyt.vkarchiver.data.remote.model.ApiWallPost

@Serializable
class WallPostsResponse(
    @SerialName("count") val count: Int?,
    @SerialName("items") val items: List<ApiWallPost>?,
)