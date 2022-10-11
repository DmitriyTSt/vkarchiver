package ru.dmitriyt.vkarchiver.presentation.ui.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import ru.dmitriyt.vkarchiver.data.model.LoadingState
import ru.dmitriyt.vkarchiver.presentation.navigation.NavigationFlow
import ru.dmitriyt.vkarchiver.presentation.navigation.Screen

abstract class BaseViewModel {
    protected val viewModelScope = CoroutineScope(Dispatchers.Main)

    protected fun navigate(screen: Screen) = viewModelScope.launch {
        NavigationFlow.screen.value = screen
    }

    protected fun <T> executeFlow(block: suspend () -> T): Flow<LoadingState<T>> = flow {
        emit(LoadingState.Loading())
        try {
            LoadingState.Success(block())
        } catch (e: Exception) {
            LoadingState.Error(e)
        }
    }

    fun <T, D> Flow<LoadingState<T>>.collectTo(destination: MutableStateFlow<D>, mapper: (LoadingState<T>) -> D) {
        viewModelScope.launch {
            collect { destination.value = mapper(it) }
        }
    }
}