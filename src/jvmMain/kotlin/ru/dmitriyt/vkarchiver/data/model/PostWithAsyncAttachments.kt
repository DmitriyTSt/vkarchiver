package ru.dmitriyt.vkarchiver.data.model

import com.vk.api.sdk.objects.wall.WallpostFull
import kotlinx.coroutines.Deferred

class PostWithAsyncAttachments(
    val post: WallpostFull,
    val attachments: List<Deferred<PostAttachment?>>,
)