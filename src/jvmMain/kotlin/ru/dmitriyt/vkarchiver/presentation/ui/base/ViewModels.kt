package ru.dmitriyt.vkarchiver.presentation.ui.base

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
        SplashViewModel::class.java -> ViewModelStorage.getOrCreate(SplashViewModel::class.java) { SplashViewModel() } as T
        else -> throw IllegalStateException("Unknown viewModel ${T::class.java}")
    }
}