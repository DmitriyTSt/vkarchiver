package ru.dmitriyt.vkarchiver.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ApiWallPostAttachmentType {
    @SerialName("photo")
    PHOTO,
    @SerialName("video")
    VIDEO,
    @SerialName("doc")
    DOC,
    @SerialName("link")
    LINK,
    @SerialName("photos_list")
    PHOTOS_LIST,
}