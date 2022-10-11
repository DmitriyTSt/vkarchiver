package ru.dmitriyt.vkarchiver.data.model

import ru.dmitriyt.vkarchiver.data.resources.StringRes

sealed class LoadingState<T> {
    class Loading<T> : LoadingState<T>()

    class Error<T>(
        val throwable: Throwable,
        val message: String = throwable.message ?: StringRes.defaultErrorMessage,
    ) : LoadingState<T>()

    class Success<T>(val data: T) : LoadingState<T>()
}