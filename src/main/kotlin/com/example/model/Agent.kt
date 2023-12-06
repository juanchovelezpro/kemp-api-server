package com.example.model

import io.ktor.websocket.*

data class Agent(
    val name: String,
    val sessionId: String,
    val socket: WebSocketSession){

    suspend fun process(request: Request){
        socket.send("You are $name, with sessionId: $sessionId")
    }

}