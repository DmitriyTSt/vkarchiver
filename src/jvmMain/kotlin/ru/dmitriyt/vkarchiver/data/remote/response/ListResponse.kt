package ru.dmitriyt.vkarchiver.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ListResponse<T>(
    @SerialName("count") val count: Int?,
    @SerialName("items") val items: List<T>?,
)