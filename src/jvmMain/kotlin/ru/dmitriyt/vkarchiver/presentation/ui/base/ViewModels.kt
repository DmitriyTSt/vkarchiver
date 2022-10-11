package ru.dmitriyt.vkarchiver.presentation.ui.base

import ru.dmitriyt.vkarchiver.domain.AuthByCodeUseCase
import ru.dmitriyt.vkarchiver.domain.GetAuthLinkUseCase
import ru.dmitriyt.vkarchiver.domain.GetCacheDirectoryUseCase
import ru.dmitriyt.vkarchiver.domain.SetCacheDirectoryUseCase
import ru.dmitriyt.vkarchiver.domain.SplashUseCase
import ru.dmitriyt.vkarchiver.presentation.ui.auth.AuthViewModel
import ru.dmitriyt.vkarchiver.presentation.ui.main.MainViewModel
import ru.dmitriyt.vkarchiver.presentation.ui.splash.SplashViewModel

object ViewModelStorage {

    private val storage = mutableMapOf<Class<*>, BaseViewModel>()

    @Suppress("UNCHECKED_CAST")
    fun <T> getOrCreate(clazz: Class<T>, constructor: () -> T): T {
        val viewModel = storage[clazz]
        return if (viewModel != null) {
            viewModel as T
        } else {
            val newViewModel = constructor()
            storage[clazz] = newViewModel as BaseViewModel
            newViewModel
        }
    }
}

inline fun <reified T : BaseViewModel> viewModels(): T {
    return when (T::class.java) {
        SplashViewModel::class.java -> {
            ViewModelStorage.getOrCreate(SplashViewModel::class.java) {
                SplashViewModel(
                    splashUseCase = SplashUseCase.new(),
                )
            } as T
        }
        AuthViewModel::class.java -> {
            ViewModelStorage.getOrCreate(AuthViewModel::class.java) {
                AuthViewModel(
                    getAuthLinkUseCase = GetAuthLinkUseCase.new(),
                    authByCodeUseCase = AuthByCodeUseCase.new(),
                )
            } as T
        }
        MainViewModel::class.java -> {
            ViewModelStorage.getOrCreate(MainViewModel::class.java) {
                MainViewModel(
                    getCacheDirectoryUseCase = GetCacheDirectoryUseCase.new(),
                    setCacheDirectoryUseCase = SetCacheDirectoryUseCase.new(),
                )
            } as T
        }
        else -> throw IllegalStateException("Unknown viewModel ${T::class.java}")
    }
}