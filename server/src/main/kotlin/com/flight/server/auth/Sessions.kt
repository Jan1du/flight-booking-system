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

@Serializable
data class BookingSession(
    val flightId: Int? = null,
    val returnFlightId: Int? = null,
    val cabinClass: String? = null,
    val numAdults: Int? = null,
    val numChildren: Int? = null,
    val passengerFirstNamesAdult: List<String> = emptyList(),
    val passengerFirstNamesChild: List<String> = emptyList(),
    val passengerLastNamesAdult: List<String> = emptyList(),
    val passengerLastNamesChild: List<String> = emptyList(),
    val selectedSeats: List<String> = emptyList(),
    val selectedSeatsReturn: List<String> = emptyList(),
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

        cookie<BookingSession>("booking_session") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = MAX_SESSION_DURATION
        }
    }
}
