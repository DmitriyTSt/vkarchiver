package ru.dmitriyt.vkarchiver.data.resources

import java.util.Locale

val StringRes = when (Locale.getDefault().language) {
    "en" -> EnStrings
    "ru" -> RuStrings
    else -> EnStrings
}

interface Strings {
    val appName: String get() = "VK Archiver"
    val selectDirectoryLabel: String
    val appInitErrorMessage: String
}

object EnStrings : Strings {
    override val selectDirectoryLabel: String = "Select directory to save"
    override val appInitErrorMessage: String = "App initialize error.\nCheck configuration file."
}

object RuStrings : Strings {
    override val appName: String = "ВК Архиватор"
    override val selectDirectoryLabel: String = "Выберите директорию для сохранения"
    override val appInitErrorMessage: String = "Ошибка инициализации приложения.\nПроверьте конфигурационный файл."
}