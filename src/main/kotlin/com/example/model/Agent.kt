package com.example.model

import io.ktor.websocket.*

class Agent(
    val name: String,
    val sessionId: String,
    val socket: WebSocketSession
) {

    suspend fun process(request: Request) {
        socket.send("You are $name, with sessionId: $sessionId")
    }

    suspend fun send(frame: Frame.Text) {
        socket.send(frame)
    }

}