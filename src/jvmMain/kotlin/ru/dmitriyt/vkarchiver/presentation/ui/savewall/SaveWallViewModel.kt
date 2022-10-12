package ru.dmitriyt.vkarchiver.presentation.ui.savewall

import com.vk.api.sdk.objects.wall.WallpostFull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.dmitriyt.vkarchiver.data.model.LoadingState
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
        _saveWallState.value = SaveWallState.Loading(0f)
        getWallPostsUseCase(domain).collect { result ->
            val state = when (result) {
                is GetWallPostsUseCase.Result.Data -> {
                    saveWallPosts(directoryPath, domain, result.items)
                    SaveWallState.Loading(1f)
                }
                is GetWallPostsUseCase.Result.Error -> SaveWallState.Error(result.t.message ?: StringRes.defaultErrorMessage)
                is GetWallPostsUseCase.Result.Progress -> SaveWallState.Loading(result.progress)
            }
            _saveWallState.value = state
        }
    }

    private fun saveWallPosts(directoryPath: String, domain: String, items: List<WallpostFull>) {
        executeFlow { saveWallPostsUseCase(directoryPath, domain, items) }.collectTo(_saveWallState) { state ->
            when (state) {
                is LoadingState.Error -> SaveWallState.Error(state.message)
                is LoadingState.Loading -> SaveWallState.Loading(1f)
                is LoadingState.Success -> SaveWallState.Success(domain, LocalDateTime.now())
            }
        }
    }
}