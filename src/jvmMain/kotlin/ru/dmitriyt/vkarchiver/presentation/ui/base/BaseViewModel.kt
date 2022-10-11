package ru.dmitriyt.vkarchiver.presentation.ui.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.dmitriyt.vkarchiver.presentation.navigation.NavigationFlow
import ru.dmitriyt.vkarchiver.presentation.navigation.Screen

abstract class BaseViewModel {
    protected val viewModelScope = CoroutineScope(Dispatchers.Main)

    protected fun navigate(screen: Screen) = viewModelScope.launch {
        NavigationFlow.screen.value = screen
    }
}