package com.example.model

import io.ktor.websocket.*
import java.util.concurrent.ConcurrentHashMap

class AgentController {
    private val agents = ConcurrentHashMap<String, Agent>()

    fun onLink(
        agentName: String,
        sessionId: String,
        socket: WebSocketSession
    ){
        if(agents.contains(agentName)){
            throw Exception("Cluster with name $agentName already exists")
        }
        agents[agentName] = Agent(agentName,sessionId,socket)
    }



}