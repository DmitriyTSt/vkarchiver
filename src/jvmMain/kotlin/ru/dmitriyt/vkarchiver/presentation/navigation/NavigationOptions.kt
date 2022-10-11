package ru.dmitriyt.vkarchiver.presentation.navigation

data class NavigationOptions(
    var popUpTo: Screen? = null,
    var popUpToInclusive: Boolean = false,
)

fun NavigationOptions(initBlock: NavigationOptions.() -> Unit): NavigationOptions {
    return NavigationOptions().apply(initBlock)
}