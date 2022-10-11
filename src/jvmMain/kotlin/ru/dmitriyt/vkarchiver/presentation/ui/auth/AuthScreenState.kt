package ru.dmitriyt.vkarchiver.presentation.ui.auth

sealed class AuthScreenState(open val link: String) {
    object Loading : AuthScreenState("")
    data class Idle(override val link: String) : AuthScreenState(link)
    data class AwaitCode(override val link: String) : AuthScreenState(link)
    data class LoginError(override val link: String, val message: String) : AuthScreenState(link)
    object LoginSuccess : AuthScreenState("")
}