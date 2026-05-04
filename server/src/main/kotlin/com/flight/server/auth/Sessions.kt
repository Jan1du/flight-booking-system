package com.flight.server.auth

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.sessions.SessionTransportTransformerEncrypt
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.util.hex
import kotlinx.serialization.Serializable
import java.security.SecureRandom

const val SESSION_KEY_LENGTH = 16
const val MAX_SESSION_DURATION: Long = 86400 // 24 hours

@Serializable
data class UserSession(
    val email: String,
)

// Used copilot to learn how to generate secure session keys (line 22-26)
// Generate random key (32 bytes = 64 hex chars)
private fun loadSessionKey(): String =
    hex(
        ByteArray(SESSION_KEY_LENGTH)
            .apply { SecureRandom().nextBytes(this) },
    )

fun Application.configureSessions() {
    val encryptKey = hex(loadSessionKey())
    val signKey = hex(loadSessionKey())
    install(Sessions) {
        cookie<UserSession>("user_session") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = MAX_SESSION_DURATION

            transform(SessionTransportTransformerEncrypt(encryptKey, signKey))
        }
    }
}
