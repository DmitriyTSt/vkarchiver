package ru.dmitriyt.vkarchiver.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ApiWallPostAttachment(
    @SerialName("type") val type: ApiWallPostAttachmentType?,
    @SerialName("photo") val photo: ApiPhoto?,
    @SerialName("video") val video: ApiVideo?,
    @SerialName("doc") val doc: ApiDoc?,
    @SerialName("link") val link: ApiLink?,
    @SerialName("photos_list") val photosList: List<String>?,
)