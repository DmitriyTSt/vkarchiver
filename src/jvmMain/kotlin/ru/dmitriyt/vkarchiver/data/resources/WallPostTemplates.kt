package ru.dmitriyt.vkarchiver.data.resources

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.dmitriyt.vkarchiver.data.model.CachedWallPost
import ru.dmitriyt.vkarchiver.data.model.CachedWallPostAttachment
import ru.dmitriyt.vkarchiver.data.model.WallPost
import ru.dmitriyt.vkarchiver.data.model.WallPostAttachment
import java.text.SimpleDateFormat

/**
 * HTML шаблон ленты сообщества
 */
object WallPostTemplates {
    suspend fun getHtmlPosts(domain: String, posts: List<CachedWallPost>): String = withContext(Dispatchers.Default) {
        """
        <html>
            <head>
                <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.2/dist/css/bootstrap.min.css" rel="stylesheet" 
                integrity="sha384-Zenh87qX5JnK2Jl0vWa8Ck2rdkQ2Bzep5IDxbcnCeuOxjzrPF/et3URy9Bv1WTRi" crossorigin="anonymous">
                <style>
					h1 {
						text-align: center;
						padding: 16px;
					}
					.post-text {
						margin-top: 16px;
					}
                    .post-attachments-original img {
                        width: 320px;
                    }
                    .post-attachments-cached img {
                        width: 320px;
                    }
                    .attachment-video img {
                        width: 64px;
                    }
					.posts-wrap {
						max-width: 1000px;
						margin: 0 auto;
					}
					.post {
						word-wrap: break-word;
						overflow-wrap: break-word;
						border: 1px solid lightgray;
						padding: 8px;
						border-radius: 8px;
                        margin-top: 16px;
                        margin-bottom: 16px;
					}
					.post-attachment-title {
						font-weight: 500;
					}
					.post-attachments-cached {
						font-weight: 500;
					}
                </style>
            </head>
            <body>
                <h1><a href="https://vk.com/$domain">https://vk.com/$domain</a></h1>
                
                <div class="posts-wrap">
                    ${posts.joinToString("\n") { post -> getPostHtml(post) }}
                </div>
            </body>
        </html>
    """.trimIndent()
    }

    private fun getPostHtml(cachedWallPost: CachedWallPost): String {
        val post = cachedWallPost.post
        val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm")
        val dateString = formatter.format((post.date ?: 0) * 1000L)
        return """
        <div class="post" id="wall${post.fromId}_${post.id}">
            <div class="post-author">Автор: ${post.fromId ?: 0}</div>
            <div class="post-date">$dateString</div>
            <div class="post-text">${formatText(post.text)}</div>
            ${getAttachmentBlockHtml(post)}
            ${getCachedAttachmentBlockHtml(cachedWallPost)}
        </div>
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

    private fun getCachedAttachmentBlockHtml(cachedWallPost: CachedWallPost): String {
        if (cachedWallPost.cachedAttachments.isEmpty()) return ""
        return """
            <hr>
            <div class="post-attachments-cached">
                <div class="post-attachment-title">Сохраненные ресурсы</div>
                ${cachedWallPost.cachedAttachments.joinToString("\n") { getCachedAttachmentHtml(it) }}
            </div>
        """.trimIndent()
    }

    private fun getCachedAttachmentHtml(cachedAttachment: CachedWallPostAttachment): String {
        return when (cachedAttachment) {
            is CachedWallPostAttachment.Image -> """
                <img src="${cachedAttachment.filePath}"/>
            """.trimIndent()
        }
    }

    private fun getAttachmentBlockHtml(post: WallPost): String {
        if (post.attachments.isEmpty()) return ""
        return """
            <hr>
            <div class="post-attachments-original">
                <div class="post-attachment-title">Внешние ресурсы</div>
                ${post.attachments.joinToString("\n") { getAttachmentHtml(it) }}
            </div>
        """.trimIndent()
    }

    private fun getAttachmentHtml(attachment: WallPostAttachment): String {
        return when (attachment) {
            is WallPostAttachment.Photo -> """
                <img src="${attachment.url}"/>
            """.trimIndent()
            is WallPostAttachment.Video -> """<div class="attachment-video">
                <a href="https://vk.com/video${attachment.ownerId}_${attachment.id}">
                    https://vk.com/video${attachment.ownerId}_${attachment.id}
                </a>
            </div>""".trimIndent()
            is WallPostAttachment.Doc ->
                """<div class="attachment-doc"><a href="${attachment.url}">${attachment.url}</a></div>"""
            is WallPostAttachment.Link ->
                """<div class="attachment-link"><a href="${attachment.url}">${attachment.url}</a></div>"""
            is WallPostAttachment.PhotoList -> """<div class="attachment-photoList">
            ${attachment.urls.joinToString("\n") { """<a href="$it">$it</a>""" }}
        """.trimIndent()
            else -> ""
        }
    }
}