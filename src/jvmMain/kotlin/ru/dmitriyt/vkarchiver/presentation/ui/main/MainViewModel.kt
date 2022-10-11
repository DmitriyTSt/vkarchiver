package ru.dmitriyt.vkarchiver.presentation.ui.main

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.dmitriyt.vkarchiver.domain.GetCacheDirectoryUseCase
import ru.dmitriyt.vkarchiver.domain.SetCacheDirectoryUseCase
import ru.dmitriyt.vkarchiver.presentation.ui.base.BaseViewModel

class MainViewModel(
    private val getCacheDirectoryUseCase: GetCacheDirectoryUseCase,
    private val setCacheDirectoryUseCase: SetCacheDirectoryUseCase,
) : BaseViewModel() {
    private val _directoryStateFlow = MutableStateFlow<DirectoryState>(DirectoryState.Loading)
    val directoryStateFlow = _directoryStateFlow.asStateFlow()

    fun loadDirectory() = viewModelScope.launch {
        _directoryStateFlow.value = DirectoryState.Data(getCacheDirectoryUseCase())
    }

    fun selectDirectory(directoryPath: String) = viewModelScope.launch {
        setCacheDirectoryUseCase(directoryPath)
        loadDirectory()
    }
}