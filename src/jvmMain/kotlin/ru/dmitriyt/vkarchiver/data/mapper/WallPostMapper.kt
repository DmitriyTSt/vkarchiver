package ru.dmitriyt.vkarchiver.data.mapper

import ru.dmitriyt.vkarchiver.data.extensions.orDefault
import ru.dmitriyt.vkarchiver.data.model.WallPost
import ru.dmitriyt.vkarchiver.data.model.WallPostAttachment
import ru.dmitriyt.vkarchiver.data.remote.model.ApiDoc
import ru.dmitriyt.vkarchiver.data.remote.model.ApiLink
import ru.dmitriyt.vkarchiver.data.remote.model.ApiPhoto
import ru.dmitriyt.vkarchiver.data.remote.model.ApiVideo
import ru.dmitriyt.vkarchiver.data.remote.model.ApiWallPost
import ru.dmitriyt.vkarchiver.data.remote.model.ApiWallPostAttachment
import ru.dmitriyt.vkarchiver.data.remote.model.ApiWallPostAttachmentType

class WallPostMapper {
    fun fromApiToModel(api: ApiWallPost): WallPost {
        return WallPost(
            id = api.id.orDefault(),
            fromId = api.fromId.orDefault(),
            date = api.date.orDefault(),
            text = api.text.orEmpty(),
            attachments = api.attachments.orEmpty().mapNotNull { fromApiToModel(it) },
        )
    }

    private fun fromApiToModel(api: ApiWallPostAttachment): WallPostAttachment? {
        return when (api.type) {
            ApiWallPostAttachmentType.PHOTO -> fromApiToModel(api.photo)
            ApiWallPostAttachmentType.VIDEO -> fromApiToModel(api.video)
            ApiWallPostAttachmentType.DOC -> fromApiToModel(api.doc)
            ApiWallPostAttachmentType.LINK -> fromApiToModel(api.link)
            ApiWallPostAttachmentType.PHOTOS_LIST -> fromApiPhotoListToModel(api.photosList)
            ApiWallPostAttachmentType.UNKNOWN -> null
            null -> null
        }
    }

    private fun fromApiToModel(api: ApiPhoto?): WallPostAttachment.Photo? {
        return api?.sizes
            ?.maxByOrNull { it.width.orDefault() }
            ?.url?.toString()
            ?.let { WallPostAttachment.Photo(it) }
    }

    private fun fromApiToModel(api: ApiVideo?): WallPostAttachment.Video? {
        return WallPostAttachment.Video(
            id = api?.id ?: return null,
            ownerId = api.ownerId ?: return null,
        )
    }

    private fun fromApiToModel(api: ApiDoc?): WallPostAttachment.Doc? {
        return WallPostAttachment.Doc(
            url = api?.url ?: return null,
        )
    }

    private fun fromApiToModel(api: ApiLink?): WallPostAttachment.Link? {
        return WallPostAttachment.Link(
            url = api?.url ?: return null,
        )
    }

    private fun fromApiPhotoListToModel(api: List<String>?): WallPostAttachment.PhotoList? {
        return WallPostAttachment.PhotoList(
            urls = api ?: return null
        )
    }
}