package ru.dmitriyt.vkarchiver.presentation.ui.savewall

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.dmitriyt.vkarchiver.presentation.ui.base.viewModels

@Composable
fun SaveWallView(modifier: Modifier = Modifier, directoryPath: String?, viewModel: SaveWallViewModel = viewModels()) {
    var groupAddress by remember { mutableStateOf("") }
    val state by viewModel.saveWallState.collectAsState()

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            TextField(
                value = groupAddress,
                modifier = Modifier.fillMaxWidth(),
                enabled = state !is SaveWallState.Loading,
                onValueChange = { groupAddress = it },
                placeholder = { Text("Введите адрес сообщества") },
            )
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = (state as? SaveWallState.Error)?.message.orEmpty(),
                color = Color.Red,
            )
        }
        Box(modifier = Modifier.padding(start = 16.dp).widthIn(100.dp)) {
            if (state !is SaveWallState.Loading) {
                Button(onClick = {
                    viewModel.loadWallPosts(directoryPath, groupAddress)
                }) {
                    Text("Сохранить")
                }
            } else {
                Column(modifier = Modifier.widthIn(100.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        modifier = Modifier.padding(top = 4.dp),
                        text = (state as SaveWallState.Loading).message,
                    )
                    CircularProgressIndicator()
                    Text(
                        modifier = Modifier.padding(top = 4.dp),
                        text = ((state as SaveWallState.Loading).progress * 100).toInt().let { "$it%" },
                    )
                }
            }
        }
    }
}