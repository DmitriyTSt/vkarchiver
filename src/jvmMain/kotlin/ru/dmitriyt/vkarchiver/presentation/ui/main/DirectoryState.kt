package ru.dmitriyt.vkarchiver.presentation.ui.main

sealed class DirectoryState {
    object Loading : DirectoryState()
    data class Data(val directoryPath: String?) : DirectoryState()
}