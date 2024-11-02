package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class Response(val type: String, val data: String)
