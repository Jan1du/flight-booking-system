package com.flight.server

import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.log
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.pebble.respondTemplate
import io.ktor.server.response.respondRedirect
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set

suspend fun ApplicationCall.loginPage() {
    respondTemplate("login.peb", model = mapOf("active_nav" to "login"))
}

suspend fun ApplicationCall.loginUser() {
    val email = principal<UserIdPrincipal>()?.name.toString()
    application.log.info("$email logged in")
    sessions.set(UserSession(email))
    respondRedirect("/manage")
}

suspend fun ApplicationCall.logout() {
    val email = principal<UserSession>()?.email.toString()
    application.log.info("User $email logged out")
    sessions.clear<UserSession>()
    respondRedirect("/")
}
