package ru.dmitriyt.vkarchiver.presentation.navigation

sealed class Screen {
    object Splash : Screen()
    object Auth : Screen()
    object Main : Screen()
}