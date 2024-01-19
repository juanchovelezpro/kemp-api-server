package com.example.model

import io.ktor.websocket.*
import java.util.concurrent.ConcurrentHashMap

class AgentController {
    private val agents = ConcurrentHashMap<String, Agent>()

    fun linkAgent(
        agentName: String,
        sessionId: String,
        socket: WebSocketSession
    ): Agent? {
        if (!agents.containsKey(agentName)) {
            agents[agentName] = Agent(agentName, sessionId, socket)
        }

        return agents[agentName]
    }

    fun getAgent(name: String): Agent? {
        return agents[name]
    }
}