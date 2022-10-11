package ru.dmitriyt.vkarchiver.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import ru.dmitriyt.vkarchiver.presentation.ui.auth.AuthScreen
import ru.dmitriyt.vkarchiver.presentation.ui.main.MainScreen
import ru.dmitriyt.vkarchiver.presentation.ui.splash.SplashScreen

@Composable
fun ScreenContainer(modifier: Modifier = Modifier, navigationState: State<Screen>) {
    val screen by remember { navigationState }
    Box(modifier) {
        when (screen) {
            Screen.Splash -> SplashScreen()
            Screen.Auth -> AuthScreen()
            Screen.Main -> MainScreen()
        }
    }
}