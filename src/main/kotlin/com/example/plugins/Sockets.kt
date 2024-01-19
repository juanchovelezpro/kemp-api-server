package com.example.plugins

import com.example.model.AgentController
import com.example.model.Request
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.receiveAsFlow
import java.time.Duration
import java.util.*

fun Application.configureSockets(agentController: AgentController, coroutineScope: CoroutineScope) {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        webSocket("/link") {
            val type = call.request.headers["type"]
            val agentName = call.request.headers["name"]

            if (type == "agent") {
                val agent = agentController.linkAgent(agentName!!, UUID.randomUUID().toString(), this)
                println("Agent: $agentName connected with sessionId ${agent?.sessionId}")
                agent?.process(Request("LIST", ""))
            }


            val incMsg = launch {
                incoming.receiveAsFlow().collect { frame ->
                    if (type == "agent") {
                        val name = call.request.headers["name"]
                        val agent = agentController.getAgent(name!!)
                        if (frame is Frame.Text) {
                            println("Receving ${frame.readText()} from $name ${agent?.sessionId}")
                            outgoing.send(Frame.Text("From server... Received ;)"))
                        }
                    } else {
                    }
                }
            }

            val outMsg = launch {
                while (isActive) {
                    val request = "list"
                    delay(5000L)
                    outgoing.send(Frame.Text(request))
                }
            }

            outMsg.join()
            incMsg.cancelAndJoin()

        }
    }
}
