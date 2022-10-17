package ru.dmitriyt.vkarchiver.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.dmitriyt.vkarchiver.data.mapper.EnumIgnoreUnknownSerializer

@Serializable(with = ApiWallPostAttachmentTypeSerializer::class)
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

    UNKNOWN,
}

private object ApiWallPostAttachmentTypeSerializer : EnumIgnoreUnknownSerializer<ApiWallPostAttachmentType>(
    ApiWallPostAttachmentType.values(),
    ApiWallPostAttachmentType.UNKNOWN,
)