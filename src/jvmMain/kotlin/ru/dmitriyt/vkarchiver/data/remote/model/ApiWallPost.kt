package ru.dmitriyt.vkarchiver.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ApiWallPost(
    @SerialName("id") val id: Int?,
    @SerialName("from_id") val fromId: Int?,
    @SerialName("date") val date: Long?,
    @SerialName("text") val text: String?,
    @SerialName("attachments") val attachments: List<ApiWallPostAttachment>?,
)