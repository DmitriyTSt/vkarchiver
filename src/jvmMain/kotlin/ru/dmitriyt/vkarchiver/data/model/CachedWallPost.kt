package ru.dmitriyt.vkarchiver.data.model

class CachedWallPost(
    val post: WallPost,
    val cachedAttachments: List<CachedWallPostAttachment>,
)