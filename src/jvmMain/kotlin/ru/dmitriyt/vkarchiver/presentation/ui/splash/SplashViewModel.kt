package ru.dmitriyt.vkarchiver.presentation.ui.splash

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.dmitriyt.vkarchiver.domain.SplashUseCase
import ru.dmitriyt.vkarchiver.presentation.navigation.Screen
import ru.dmitriyt.vkarchiver.presentation.ui.base.BaseViewModel

class SplashViewModel(
    private val splashUseCase: SplashUseCase,
) : BaseViewModel() {
    private val _splashStateFlow = MutableStateFlow<SplashUseCase.Result?>(null)
    val splashStateFlow = _splashStateFlow.asStateFlow()

    fun checkAppEnv() = viewModelScope.launch {
        _splashStateFlow.value = splashUseCase()
    }

    fun openScreen(splashResult: SplashUseCase.Result) {
        when (splashResult) {
            SplashUseCase.Result.MAIN -> navigate(Screen.Main)
            SplashUseCase.Result.AUTH -> navigate(Screen.Auth)
            SplashUseCase.Result.ERROR -> Unit
        }
    }
}