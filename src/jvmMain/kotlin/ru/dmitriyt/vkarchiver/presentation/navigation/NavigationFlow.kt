package ru.dmitriyt.vkarchiver.presentation.navigation

import kotlinx.coroutines.flow.MutableStateFlow

object NavigationFlow {
    val screen: MutableStateFlow<Screen> = MutableStateFlow(Screen.Splash)
}