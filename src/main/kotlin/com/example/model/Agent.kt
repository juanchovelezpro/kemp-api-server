package com.example.model

import io.ktor.websocket.*

data class Agent(
    val name: String,
    val sessionId: String,
    val socket: WebSocketSession){
}