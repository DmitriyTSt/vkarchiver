package ru.dmitriyt.vkarchiver.data.model

sealed class WallPostAttachment {
    class Photo(val url: String) : WallPostAttachment()
    class Video(val id: Int, val ownerId: Int) : WallPostAttachment()
    class Doc(val url: String) : WallPostAttachment()
    class Link(val url: String) : WallPostAttachment()
    class PhotoList(val urls: List<String>) : WallPostAttachment()
}