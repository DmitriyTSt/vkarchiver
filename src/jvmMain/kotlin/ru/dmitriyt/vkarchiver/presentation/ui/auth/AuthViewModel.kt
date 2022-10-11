package ru.dmitriyt.vkarchiver.presentation.ui.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.dmitriyt.vkarchiver.data.model.LoadingState
import ru.dmitriyt.vkarchiver.domain.AuthByCodeUseCase
import ru.dmitriyt.vkarchiver.domain.GetAuthLinkUseCase
import ru.dmitriyt.vkarchiver.presentation.navigation.NavigationOptions
import ru.dmitriyt.vkarchiver.presentation.navigation.Screen
import ru.dmitriyt.vkarchiver.presentation.ui.base.BaseViewModel

class AuthViewModel(
    private val getAuthLinkUseCase: GetAuthLinkUseCase,
    private val authByCodeUseCase: AuthByCodeUseCase,
) : BaseViewModel() {
    private val _screenStateStateFlow = MutableStateFlow<AuthScreenState>(AuthScreenState.Loading)
    val screenStateStateFlow = _screenStateStateFlow.asStateFlow()

    private var authLink = ""

    fun loadLink() = viewModelScope.launch {
        authLink = getAuthLinkUseCase()
        _screenStateStateFlow.value = AuthScreenState.Idle(authLink)
    }

    fun onLinkClick() = viewModelScope.launch {
        _screenStateStateFlow.value = AuthScreenState.AwaitCode(authLink)
    }

    fun openMain() {
        navigate(Screen.Main, NavigationOptions(popUpTo = Screen.Auth, popUpToInclusive = true))
    }

    fun loginByCode(code: String) {
        if (code.isEmpty()) {
            _screenStateStateFlow.value = AuthScreenState.LoginError(authLink, "Введите код")
            return
        }

        executeFlow { authByCodeUseCase(code) }.collectTo(_screenStateStateFlow) { state ->
            when (state) {
                is LoadingState.Error -> AuthScreenState.LoginError(authLink, state.message)
                is LoadingState.Loading -> AuthScreenState.Loading
                is LoadingState.Success -> AuthScreenState.LoginSuccess
            }
        }
    }
}