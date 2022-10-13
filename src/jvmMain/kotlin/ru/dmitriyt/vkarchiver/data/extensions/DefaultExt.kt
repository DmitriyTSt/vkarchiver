package ru.dmitriyt.vkarchiver.data.extensions

fun Int?.orDefault(): Int = this ?: 0

fun Long?.orDefault(): Long = this ?: 0