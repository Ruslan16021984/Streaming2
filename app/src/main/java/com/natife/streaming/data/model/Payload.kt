package com.natife.streaming.data.model

data class Payload(
    val iss: String?,
    val aud: List<String>?,
    val iat: Long?,
    val exp: Long?,
    val authTime: Long?,
    val sub: Int?,
    val email: String?
)
