import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.vk.api.sdk.client.actors.UserActor
import com.vk.api.sdk.objects.wall.WallpostFull
import com.vk.api.sdk.objects.wall.responses.GetResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.dmitriyt.vkarchiver.data.model.LoadingState
import ru.dmitriyt.vkarchiver.data.resources.StringRes
import ru.dmitriyt.vkarchiver.domain.AuthByCodeUseCase
import ru.dmitriyt.vkarchiver.domain.SaveWallPostsUseCase
import java.io.File

private const val GET_CODE_LINK = "https://oauth.vk.com/authorize?client_id=APP_ID&redirect_uri=http://localhost"

@Composable
@Preview
fun App() {
    var directory by remember { mutableStateOf<File?>(null) }
    var actor by remember { mutableStateOf<UserActor?>(null) }
    var authCode by remember { mutableStateOf("") }
    var groupAddress by remember { mutableStateOf("") }
    var startDownload by remember { mutableStateOf("-1") }
    val coroutineScope = rememberCoroutineScope()

    MaterialTheme {
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
            Row {
                Text(
                    text = (if (directory == null) StringRes.selectDirectoryLabel else directory.toString()),
                    modifier = Modifier.align(Alignment.CenterVertically)
                        .weight(1f)
                )
                DirectorySelectorButton(
                    text = if (directory == null) "Выбрать" else "Изменить",
                    oldDirectory = directory,
                    onSelect = { directory = it },
                )
            }

            val uriHandler = LocalUriHandler.current

            val userActor = actor
            if (userActor != null) {
                Text("Вы вошли в ВК")
                TextField(
                    groupAddress,
                    onValueChange = { groupAddress = it },
                    placeholder = { Text("Введите адрес сообщества") },
                )
                Button(onClick = {
                    startDownload = groupAddress
                }) {
                    Text("Скачать стену сообщества")
                }
                if (startDownload == groupAddress) {
                    val statePosts = loadingAllWallPost(userActor, groupAddress)
                    when (statePosts.value) {
                        is LoadingState.Error -> Text(
                            (statePosts.value as LoadingState.Error<Pair<List<WallpostFull>, Int>>).message ?: "Ошибка",
                            color = Color.Red,
                        )
                        is LoadingState.Loading -> {
                            Text("Загрузка...")
                        }
                        is LoadingState.Success -> {
                            (statePosts.value as LoadingState.Success<Pair<List<WallpostFull>, Int>>).data.let { (posts, total) ->
                                if (posts.size != total) {
                                    Text("${posts.size} / $total ...")
                                } else {
                                    val savePostsState = savePosts(groupAddress, posts, directory)
                                    when (savePostsState.value) {
                                        is LoadingState.Error -> Text(
                                            (savePostsState.value as LoadingState.Error<String>).message ?: "Ошибка",
                                            color = Color.Red,
                                        )
                                        is LoadingState.Loading -> Text("Сохранение...")
                                        is LoadingState.Success -> {
                                            Text("Сохранено: ${(savePostsState.value as LoadingState.Success<String>).data}")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Text("Перейдите по ссылке, чтобы получить code")
                val annotatedLink = buildAnnotatedString {
                    append(GET_CODE_LINK)
                    addStyle(
                        style = SpanStyle(
                            color = Color(0xff64B5F6),
                            textDecoration = TextDecoration.Underline
                        ),
                        start = 0,
                        end = GET_CODE_LINK.length,
                    )
                    addStringAnnotation(
                        tag = "URL",
                        annotation = GET_CODE_LINK,
                        start = 0,
                        end = GET_CODE_LINK.length,
                    )
                }
                ClickableText(text = annotatedLink, modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)) {
                    uriHandler.openUri(annotatedLink.getStringAnnotations("URL", it, it).first().item)
                }
                TextField(authCode, onValueChange = { authCode = it }, placeholder = { Text("code") })
                Button(onClick = {
                    coroutineScope.launch {
                        actor = auth(authCode)
                    }
                }) {
                    Text("VK LOGIN")
                }
            }
        }
    }
}

fun main() = application {
    Window(title = StringRes.appName, onCloseRequest = ::exitApplication) {
        App()
    }
}

private suspend fun auth(code: String): UserActor? = withContext(Dispatchers.IO) {
    try {
        AuthByCodeUseCase.instance(code)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private suspend fun loadWallPosts(userActor: UserActor, groupAddress: String, offset: Int, limit: Int): GetResponse =
    withContext(Dispatchers.IO) {
        val domain = groupAddress.replace("https://vk.com/", "")
        VkApi.vk.wall().get(userActor)
            .domain(domain)
            .offset(offset)
            .count(limit)
            .execute()
    }

@Composable
private fun loadingAllWallPost(userActor: UserActor, groupAddress: String): State<LoadingState<Pair<List<WallpostFull>, Int>>> {
    return produceState<LoadingState<Pair<List<WallpostFull>, Int>>>(initialValue = LoadingState.Loading(), groupAddress) {
        val firstPartResponse = try {
            loadWallPosts(userActor, groupAddress, 0, 50)
        } catch (e: Exception) {
            value = LoadingState.Error(e.message)
            return@produceState
        }

        val total = firstPartResponse.count
        val allPosts = mutableListOf<WallpostFull>()
        allPosts.addAll(firstPartResponse.items)

        value = LoadingState.Success(allPosts to total)

        repeat((total / 50)) {
            val partResponse = try {
                loadWallPosts(userActor, groupAddress, 50 + 50 * it, 50)
            } catch (e: Exception) {
                value = LoadingState.Error(e.message)
                return@produceState
            }

            allPosts.addAll(partResponse.items)
            value = LoadingState.Success(allPosts to total)
        }
    }
}

@Composable
private fun savePosts(groupAddress: String, posts: List<WallpostFull>, directory: File?): State<LoadingState<String>> {
    val domain = groupAddress.replace("https://vk.com/", "")
    return produceState<LoadingState<String>>(LoadingState.Loading(), groupAddress) {
        if (directory == null) {
            value = LoadingState.Error("Директория не выбрана")
        } else {
            val fileName = SaveWallPostsUseCase.instance(directory.absolutePath, domain, posts)

            value = LoadingState.Success(fileName)
        }
    }
}
