package ru.dmitriyt.vkarchiver.data.extensions

fun <T> T?.orThrow(message: String): T {
    return this ?: throw IllegalStateException(message)
}