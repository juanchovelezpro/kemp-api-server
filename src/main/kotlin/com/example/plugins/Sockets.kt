package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.routing
import io.ktor.server.websocket.*
import io.ktor.websocket.DefaultWebSocketSession
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap

fun Application.configureSockets(coroutineScope: CoroutineScope) {
    install(WebSockets) {

    }
}
