package ru.dmitriyt.vkarchiver.data.resources

object Logger {
    fun d(message: String) {
        println("DEBUG $message")
    }

    fun e(e: Throwable) {
        e.printStackTrace()
    }
}