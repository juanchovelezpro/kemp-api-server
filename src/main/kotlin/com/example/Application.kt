package com.example

import com.example.model.Command
import com.example.model.Response
import com.example.plugins.*
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.receiveDeserialized
import io.ktor.server.websocket.sendSerialized
import io.ktor.server.websocket.timeout
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.DefaultWebSocketSession
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
            pingPeriod = 15.toDuration(DurationUnit.SECONDS)
            timeout = 15.toDuration(DurationUnit.SECONDS)
            maxFrameSize = Long.MAX_VALUE
            masking = false
        }
        routing {
            val clientSessions = ConcurrentHashMap<String, DefaultWebSocketSession>()

            webSocket("/connect") {
                val clientId = call.request.queryParameters["clientId"] ?: generateClientId()
                clientSessions[clientId] = this
                println("Client connected: $clientId")

                try {
                    launch {
                        while (true) {
                            delay(3000)
                            val command = Command("ping", "Ping from Server")
                            sendSerialized(command)
                            println("Ping sent to client: $clientId")
                        }
                    }

                    while (true) {
                        incoming.consumeEach {
                            val response = receiveDeserialized<Response>()
                            println("Type:${response.type} Data:${response.data}")
                        }
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }

            }
        }


    }.start(wait = true)
}

fun Application.module() {
    configureSockets(this)
    configureSerialization()
    configureHTTP()
    configureRouting()
}

fun generateClientId(): String = java.util.UUID.randomUUID().toString()