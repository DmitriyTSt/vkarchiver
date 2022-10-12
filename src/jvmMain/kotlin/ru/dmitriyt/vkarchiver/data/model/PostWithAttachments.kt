package ru.dmitriyt.vkarchiver.data.model

import com.vk.api.sdk.objects.wall.WallpostFull

class PostWithAttachments(
    val post: WallpostFull,
    val attachments: List<PostAttachment>,
)