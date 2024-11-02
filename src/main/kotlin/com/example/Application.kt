package com.example

import com.example.plugins.*
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.DefaultWebSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
            //pingPeriod = 15.toDuration(DurationUnit.SECONDS)
            //timeout = 15.toDuration(DurationUnit.SECONDS)
            maxFrameSize = Long.MAX_VALUE
            masking = false
        }
        routing {
            val uiSessions = ConcurrentHashMap<String, DefaultWebSocketSession>()
            val clientSessions = ConcurrentHashMap<String, DefaultWebSocketSession>()
            var clientId = ""
            var uiSessionId = ""
            webSocket("/connect") {
                clientId = call.request.queryParameters["clientId"] ?: generateClientId()
                clientSessions[clientId] = this
                println("Client connected: $clientId")

                try {
//                    launch {
//                        while (true) {
//                            delay(3000)
//                            val command = Command("ping", "Ping from Server")
//                            send("Ping")
//                            println("Ping sent to client: $clientId")
//                        }
//                    }

                    //while (true) {
                    incoming.consumeEach {
                        //val response = receiveDeserialized<Response>()
                        //println("Type:${response.type} Data:${response.data}")
                        if (it is Frame.Text) {
                            uiSessions[uiSessionId]?.send(it.readText())
                        }

                    }
                    //}
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }

            }

            webSocket("/ui") {
                uiSessionId = generateClientId()
                uiSessions[uiSessionId] = this
                incoming.consumeEach {
                    if (it is Frame.Text) {
                        send("Retrieving ${it.readText()}")
                        clientSessions[clientId]?.send(it.readText())
                    }
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