package ru.dmitriyt.vkarchiver.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class BaseError(
    @SerialName("error_code") val errorCode: Int?,
    @SerialName("error_msg") val errorMessage: String?,
)