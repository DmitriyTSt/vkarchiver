package ru.dmitriyt.vkarchiver.data.resources

import com.vk.api.sdk.objects.wall.WallpostAttachment
import com.vk.api.sdk.objects.wall.WallpostAttachmentType
import com.vk.api.sdk.objects.wall.WallpostFull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat

/**
 * HTML шаблон ленты сообщества
 */
object WallPostTemplates {
    suspend fun getHtmlPosts(domain: String, posts: List<WallpostFull>): String = withContext(Dispatchers.Default) {
        """
        <html>
            <head>
                <style>
                    .attachment-photo img {
                        width: 300px;
                    }
                </style>
            </head>
            <body>
                <h1><a href="https://vk.com/$domain">https://vk.com/$domain</a></h1>
                
                <div class="posts-wrap">
                    ${posts.map { post -> getPostHtml(post) }.joinToString("\n")}
                </div>
            </body>
        </html>
    """.trimIndent()
    }

    private fun getPostHtml(post: WallpostFull): String {
        val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm")
        val dateString = formatter.format((post.date ?: 0) * 1000L)
        return """
        <div class="post">
            <div class="post-author">Автор: ${post.fromId ?: 0}</div>
            <div class="post-date">$dateString</div>
            <div class="post-text">${post.text ?: ""}</div>
            <div class="post-attachments">
                ${post.attachments.orEmpty().map { getAttachmentHtml(it) }.joinToString("\n")}
            </div>
        </div>
        <hr/>
    """.trimIndent()
    }

    private fun getAttachmentHtml(attachment: WallpostAttachment): String {
        return when (attachment.type) {
            WallpostAttachmentType.PHOTO -> """<div class="attachment-photo">
                <img src="${attachment.photo.sizes.maxByOrNull { it.width }?.url}"/>
                ${attachment.photo.images.orEmpty().map { """<a href="${it.url}">${it.url}</a>""" }.joinToString("\n")}
            </div>""".trimIndent()
            WallpostAttachmentType.VIDEO -> """<div class="attachment-video">
                <img src="${attachment.video.firstFrame?.maxByOrNull { it.width }?.url}"/>
                ${attachment.video.ownerId}_${attachment.video.id}
                ${attachment.video.trackCode}
            </div>""".trimIndent()
            WallpostAttachmentType.DOC ->
                """<div class="attachment-doc"><a href="${attachment.doc.url}">${attachment.doc.url}</a></div>"""
            WallpostAttachmentType.LINK ->
                """<div class="attachment-link"><a href="${attachment.link.url}">${attachment.link.url}</a></div>"""
            WallpostAttachmentType.PHOTOS_LIST -> """<div class="attachment-photoList">
            ${attachment.photosList.map { """<a href="$it">$it</a>""" }.joinToString("\n")}
        """.trimIndent()
            else -> ""
        }
    }
}