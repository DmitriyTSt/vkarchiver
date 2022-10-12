import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ru.dmitriyt.vkarchiver.data.resources.StringRes
import ru.dmitriyt.vkarchiver.presentation.ui.VkArchiverApp

fun main() = application {
    Window(title = StringRes.appName, onCloseRequest = ::exitApplication) {
        VkArchiverApp()
    }
}