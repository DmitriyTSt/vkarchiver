package ru.dmitriyt.vkarchiver.data.model

sealed class PostAttachment {
    data class Image(val filePath: String) : PostAttachment()
}