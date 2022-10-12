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
        <div class="post" id="wall${post.fromId}_${post.id}">
            <div class="post-author">Автор: ${post.fromId ?: 0}</div>
            <div class="post-date">$dateString</div>
            <div class="post-text">${post.text?.let(::formatText) ?: ""}</div>
            <div class="post-attachments">
                ${post.attachments.orEmpty().joinToString("\n") { getAttachmentHtml(it) }}
            </div>
        </div>
        <hr/>
    """.trimIndent()
    }

    private const val VK_WALL_LINK_TEMPLATE = "\\[(https://vk\\.com/wall[^| ]*)\\|([^]]+)]"
    private const val VK_LINK_TEMPLATE = "\\[(https://[^| ]*)\\|([^]]+)]"
    private const val LINK_SPACE_TEMPLATE = " https://[^ \\n]*"
    private const val LINK_ENTER_TEMPLATE = "[\\n]https://[^ \\n]*"

    private fun formatText(text: String): String {
        return text
            .replace(Regex(VK_WALL_LINK_TEMPLATE)) { result ->
                val wallPostLink = result.groupValues.getOrNull(1)?.replace("https://vk.com/", "")
                val label = result.groupValues.getOrNull(2)
                "<a href=\"#${wallPostLink.orEmpty()}\">${label ?: wallPostLink}</a>"
            }
            .replace(Regex(VK_LINK_TEMPLATE)) { result ->
                val link = result.groupValues.getOrNull(1)
                val label = result.groupValues.getOrNull(2)
                "<a href=\"${link.orEmpty()}\">${label ?: link}</a>"
            }
            .replace(Regex(LINK_SPACE_TEMPLATE)) { result ->
                val link = result.groupValues.getOrNull(0)?.substring(1)
                " <a href=\"${link.orEmpty()}\">$link</a> "
            }
            .replace(Regex(LINK_ENTER_TEMPLATE)) { result ->
                val link = result.groupValues.getOrNull(0)?.substring(1)
                "\n<a href=\"${link.orEmpty()}\">$link</a> "
            }
            .replace("\n", "<br>")
    }

    private fun getAttachmentHtml(attachment: WallpostAttachment): String {
        return when (attachment.type) {
            WallpostAttachmentType.PHOTO -> """<div class="attachment-photo">
                <img width=300 src="${attachment.photo.sizes.maxByOrNull { it.width }?.url}"/>
                ${attachment.photo.images.orEmpty().joinToString("\n") { """<a href="${it.url}">${it.url}</a>""" }}
            </div>""".trimIndent()
            WallpostAttachmentType.VIDEO -> """<div class="attachment-video">
                <img width=300 src="${attachment.video.firstFrame?.maxByOrNull { it.width }?.url}"/>
                ${attachment.video.ownerId}_${attachment.video.id}
                ${attachment.video.trackCode}
            </div>""".trimIndent()
            WallpostAttachmentType.DOC ->
                """<div class="attachment-doc"><a href="${attachment.doc.url}">${attachment.doc.url}</a></div>"""
            WallpostAttachmentType.LINK ->
                """<div class="attachment-link"><a href="${attachment.link.url}">${attachment.link.url}</a></div>"""
            WallpostAttachmentType.PHOTOS_LIST -> """<div class="attachment-photoList">
            ${attachment.photosList.joinToString("\n") { """<a href="$it">$it</a>""" }}
        """.trimIndent()
            else -> ""
        }
    }
}