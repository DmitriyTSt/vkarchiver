package ru.dmitriyt.vkarchiver.presentation.ui.main

import DirectorySelectorButton
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.dmitriyt.vkarchiver.data.resources.StringRes
import ru.dmitriyt.vkarchiver.presentation.ui.base.viewModels
import ru.dmitriyt.vkarchiver.presentation.ui.savewall.SaveWallView

@Composable
fun MainScreen(viewModel: MainViewModel = viewModels()) {
    val directoryState by viewModel.directoryStateFlow.collectAsState()

    LaunchedEffect(null) {
        viewModel.loadDirectory()
    }

    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
        when (directoryState) {
            DirectoryState.Loading -> Box(contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            is DirectoryState.Data -> Row {
                val directory = (directoryState as DirectoryState.Data).directoryPath
                Text(
                    text = (directory ?: StringRes.selectDirectoryLabel),
                    modifier = Modifier.align(Alignment.CenterVertically)
                        .weight(1f)
                )
                DirectorySelectorButton(
                    text = if (directory == null) "Выбрать" else "Изменить",
                    oldDirectoryPath = directory,
                    onSelect = viewModel::selectDirectory,
                )
            }
        }
        SaveWallView(modifier = Modifier.fillMaxWidth(), (directoryState as? DirectoryState.Data)?.directoryPath)
    }
}