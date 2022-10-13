package ru.dmitriyt.vkarchiver.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class BaseResponse<T>(
    @SerialName("response") val response: T
)