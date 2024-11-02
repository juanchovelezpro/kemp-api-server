package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.*

fun Application.configureSockets(coroutineScope: CoroutineScope) {
    install(WebSockets) {

    }
}
