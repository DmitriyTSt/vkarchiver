package ru.dmitriyt.vkarchiver.data.model

sealed class CachedWallPostAttachment {
    data class Image(val filePath: String) : CachedWallPostAttachment()
}