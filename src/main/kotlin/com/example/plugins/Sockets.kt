package com.example.plugins

import com.example.model.AgentController
import com.example.model.Request
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.receiveAsFlow
import java.lang.Exception
import java.time.Duration
import java.util.*

fun Application.configureSockets(agentController: AgentController) {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        webSocket("/link") {
            val agentName = call.receive<String>()
            val agent = agentController.linkAgent(agentName, UUID.randomUUID().toString(), this)
            println("$agentName and $agent")
            agent?.process(Request("LIST", ""))
            try {
                incoming.receiveAsFlow().collect { frame ->
                    if (frame is Frame.Text){
                        println(frame.readText())
                    }
                }
            }catch (e: Exception){
                e.printStackTrace()
            } finally {
                close()
            }
        }
    }
}
