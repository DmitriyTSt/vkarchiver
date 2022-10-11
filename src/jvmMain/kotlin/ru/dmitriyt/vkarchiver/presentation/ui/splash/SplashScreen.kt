package ru.dmitriyt.vkarchiver.presentation.ui.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ru.dmitriyt.vkarchiver.data.resources.StringRes
import ru.dmitriyt.vkarchiver.domain.SplashUseCase
import ru.dmitriyt.vkarchiver.presentation.ui.base.viewModels

@Composable
fun SplashScreen(viewModel: SplashViewModel = viewModels()) {
    val splashResult by viewModel.splashStateFlow.collectAsState(null)

    LaunchedEffect(null) {
        viewModel.checkAppEnv()
    }

    if (splashResult == SplashUseCase.Result.ERROR) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Ошибка инициализации приложения.\nПроверьте конфигурационный файл.",
                modifier = Modifier.align(Alignment.Center)
            )
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(StringRes.appName, modifier = Modifier.align(Alignment.Center))
        }
        val currentSplashResult = splashResult
        if (currentSplashResult != null) {
            LaunchedEffect(null) {
                viewModel.openScreen(currentSplashResult)
            }
        }
    }
}