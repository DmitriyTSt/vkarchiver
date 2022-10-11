package ru.dmitriyt.vkarchiver.presentation.ui.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import ru.dmitriyt.vkarchiver.presentation.ui.base.viewModels

@Composable
fun AuthScreen(viewModel: AuthViewModel = viewModels()) {
    val state by viewModel.screenStateStateFlow.collectAsState()
    var authCode by remember { mutableStateOf("") }

    LaunchedEffect(null) {
        viewModel.loadLink()
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (state == AuthScreenState.Loading) {
            CircularProgressIndicator()
        } else {
            Column(modifier = Modifier.fillMaxHeight().padding(top = 32.dp)) {
                AuthLink(viewModel, state.link)
                if (state is AuthScreenState.AwaitCode || state is AuthScreenState.LoginError) {
                    TextField(authCode, onValueChange = { authCode = it }, placeholder = { Text("code") })
                    Button(onClick = {
                        viewModel.loginByCode(authCode)
                    }) {
                        Text("Войти")
                    }

                    if (state is AuthScreenState.LoginError) {
                        Text(text = (state as AuthScreenState.LoginError).message, color = Color.Red)
                    }
                }
            }
            if (state is AuthScreenState.LoginSuccess) {
                LaunchedEffect(null) {
                    viewModel.openMain()
                }
            }
        }
    }
}

@Composable
private fun AuthLink(viewModel: AuthViewModel, link: String) {
    val uriHandler = LocalUriHandler.current
    Text("Перейдите по ссылке, чтобы получить code")
    val annotatedLink = buildAnnotatedString {
        append(link)
        addStyle(
            style = SpanStyle(
                color = Color(0xff64B5F6),
                textDecoration = TextDecoration.Underline
            ),
            start = 0,
            end = link.length,
        )
        addStringAnnotation(
            tag = "URL",
            annotation = link,
            start = 0,
            end = link.length,
        )
    }
    ClickableText(text = annotatedLink, modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)) {
        uriHandler.openUri(annotatedLink.getStringAnnotations("URL", it, it).first().item)
        viewModel.onLinkClick()
    }
}