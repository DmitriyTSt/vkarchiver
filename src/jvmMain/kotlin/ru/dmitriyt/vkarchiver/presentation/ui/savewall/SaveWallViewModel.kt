package ru.dmitriyt.vkarchiver.presentation.ui.savewall

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import ru.dmitriyt.vkarchiver.data.model.LoadingState
import ru.dmitriyt.vkarchiver.data.model.WallPost
import ru.dmitriyt.vkarchiver.data.resources.Logger
import ru.dmitriyt.vkarchiver.data.resources.StringRes
import ru.dmitriyt.vkarchiver.domain.GetWallPostsUseCase
import ru.dmitriyt.vkarchiver.domain.SaveWallPostsUseCase
import ru.dmitriyt.vkarchiver.presentation.ui.base.BaseViewModel
import java.time.LocalDateTime

class SaveWallViewModel(
    private val getWallPostsUseCase: GetWallPostsUseCase,
    private val saveWallPostsUseCase: SaveWallPostsUseCase,
) : BaseViewModel() {
    private val _saveWallState = MutableStateFlow<SaveWallState>(SaveWallState.Idle)
    val saveWallState = _saveWallState.asStateFlow()

    fun loadWallPosts(directoryPath: String?, groupAddress: String) = viewModelScope.launch {
        if (directoryPath == null) {
            _saveWallState.value = SaveWallState.Error("Выберите директорию для сохранения")
            return@launch
        }
        val domain = groupAddress.replace("https://vk.com/", "")
        _saveWallState.value = SaveWallState.Loading("Загрузка", 0f)
        getWallPostsUseCase(domain).collect { result ->
            val state = when (result) {
                is GetWallPostsUseCase.Result.Data -> {
                    saveWallPosts(directoryPath, domain, result.items)
                    SaveWallState.Loading("Загрузка", 1f)
                }
                is GetWallPostsUseCase.Result.Error -> SaveWallState.Error(result.t.message ?: StringRes.defaultErrorMessage)
                is GetWallPostsUseCase.Result.Progress -> SaveWallState.Loading("Загрузка", result.progress)
            }
            _saveWallState.value = state
        }
    }

    private fun saveWallPosts(directoryPath: String, domain: String, items: List<WallPost>) = viewModelScope.launch {
        Logger.d("start save wall posts")
        executeFlow { saveWallPostsUseCase(directoryPath, domain, items) }.collect { loadableState ->
            when (loadableState) {
                is LoadingState.Error -> _saveWallState.value = SaveWallState.Error(loadableState.message)
                is LoadingState.Loading -> _saveWallState.value = SaveWallState.Loading("Кеширование", 0f)
                is LoadingState.Success -> loadableState.data.collect { result ->
                    val state = when (result) {
                        is SaveWallPostsUseCase.Result.Error -> SaveWallState.Error(
                            result.t.message ?: StringRes.defaultErrorMessage
                        )
                        is SaveWallPostsUseCase.Result.Progress -> SaveWallState.Loading("Кеширование", result.progress)
                        is SaveWallPostsUseCase.Result.Data -> SaveWallState.Success(domain, LocalDateTime.now())
                    }
                    _saveWallState.value = state
                }
            }
        }
    }
}