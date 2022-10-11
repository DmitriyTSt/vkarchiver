package ru.dmitriyt.vkarchiver.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import ru.dmitriyt.vkarchiver.presentation.ui.auth.AuthScreen
import ru.dmitriyt.vkarchiver.presentation.ui.splash.SplashScreen

@Composable
fun ScreenContainer(modifier: Modifier, navigationState: MutableState<Screen>) {
    val screen by remember { navigationState }
    when (screen) {
        Screen.Splash -> SplashScreen()
        Screen.Auth -> AuthScreen()
    }
}