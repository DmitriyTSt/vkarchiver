package ru.dmitriyt.vkarchiver.data.model

class WallPost(
    val id: Int,
    val fromId: Int,
    val date: Long,
    val text: String,
    val attachments: List<WallPostAttachment>,
)