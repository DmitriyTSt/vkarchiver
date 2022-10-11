package ru.dmitriyt.vkarchiver.presentation.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.dmitriyt.vkarchiver.data.resources.Logger
import java.util.Deque

object Router {
    private val backStack: Deque<Screen> = java.util.ArrayDeque<Screen>().apply {
        push(Screen.Splash)
    }
    private val currentScreen: MutableStateFlow<Screen> = MutableStateFlow(Screen.Splash)

    val screen = currentScreen.asStateFlow()

    fun navigate(screen: Screen, options: NavigationOptions) {
        if (options.popUpTo != null) {
            var tmpScreen = backStack.peek()
            while (tmpScreen != options.popUpTo) {
                backStack.pop()
                tmpScreen = backStack.peek()
            }
            if (options.popUpToInclusive) {
                backStack.pop()
            }
        }
        backStack.push(screen)
        currentScreen.value = screen
        Logger.d("backStack : \n${backStack.toList().joinToString("\n") { "\t${it}" }}")
    }

    fun back() {
        backStack.pop()
        val prevScreen = backStack.peek()
        if (prevScreen != null) {
            currentScreen.value = prevScreen
        }
    }
}