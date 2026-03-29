package com.flight.server.auth

import com.flight.server.repos.checkPass
import com.flight.server.repos.findUser
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.form
import io.ktor.server.auth.session
import io.ktor.server.pebble.respondTemplate
import io.ktor.server.response.respondRedirect
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction

fun Application.configureAuthentication() {
    install(Authentication) {
        form("auth-form") {
            userParamName = "email"
            passwordParamName = "password"
            validate { credentials ->
                suspendTransaction {
                    when (checkPass(credentials)) {
                        true -> UserIdPrincipal(credentials.name.lowercase().trim())
                        false -> null
                    }
                }
            }
            challenge {
                call.respondTemplate(
                    "login.peb",
                    model =
                        mapOf(
                            "active_nav" to "login",
                            "error" to true,
                        ),
                )
            }
        }

        session<UserSession>("auth-session") {
            validate { session ->
                suspendTransaction {
                    when {
                        findUser(session.email) != null -> session
                        else -> null
                    }
                }
            }
            challenge {
                call.respondRedirect("/login")
            }
        }
    }
}
