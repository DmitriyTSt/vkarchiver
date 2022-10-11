package ru.dmitriyt.vkarchiver.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import ru.dmitriyt.vkarchiver.presentation.navigation.NavigationFlow
import ru.dmitriyt.vkarchiver.presentation.navigation.ScreenContainer

@Composable
fun VkArchieverApp() {
    val navigation = NavigationFlow.screen.collectAsState()
    ScreenContainer(navigationState = navigation)
}