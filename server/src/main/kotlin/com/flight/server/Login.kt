package com.flight.server

import com.flight.db.DatabaseFactory
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.log
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.pebble.respondTemplate
import io.ktor.server.response.respondRedirect
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

suspend fun ApplicationCall.loginPage() {
    respondTemplate("login.peb", model = mapOf("active_nav" to "login"))
}

suspend fun ApplicationCall.loginUser() {
    val email = principal<UserIdPrincipal>()?.name.toString()
    val user = transaction(DatabaseFactory.db) {
        findUser(email)
    }
    application.log.info("$email logged in")
    sessions.set(UserSession(email))

    // user is non-null because the user email is always in the database if the user managed to successfully log in
    if (user!!.firstName == null) {
        respondRedirect("/user-info")
    } else {
        respondRedirect("/manage")
    }
}