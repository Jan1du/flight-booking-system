package com.flight.server

import com.flight.db.DatabaseFactory
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.*
import io.ktor.server.pebble.respondTemplate
import io.ktor.server.response.respondRedirect
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.get
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

fun Application.configureRouting() {
    routing {
        get("/") { call.displaySearchForm(call.isLoggedIn()) }
        authenticate("auth-session") {
            get("/manage") { call.displayManage() }
            get("/user-info") { call.userInfoPage() }
            post("/user-info") { call.addUserInfo() }
            get("/logout") { call.logout() }
        }
        get("/about") { call.displayAbout(call.isLoggedIn()) }
        get("/register") { call.registerPage() }
        post("/register") { call.registerUser() }
        get("/login") { call.loginPage() }
        authenticate("auth-form") {
            post("/login") { call.loginUser() }
        }
        get("/search") { call.displayResults(call.isLoggedIn()) }
    }
}

fun ApplicationCall.isLoggedIn() = sessions.get<UserSession>() != null

private suspend fun ApplicationCall.displaySearchForm(loggedIn: Boolean) {
    respondTemplate("search-form.peb", model = mapOf(
        "active_nav" to "book",
        "logged_in" to loggedIn
    ))
}

private suspend fun ApplicationCall.displayResults(loggedIn: Boolean) {
    respondTemplate("flight-results.peb", model = mapOf(
        "active_nav" to "book",
        "logged_in" to loggedIn
    ))
}

private suspend fun ApplicationCall.displayManage() {
    // user and session should always be non-null since the manage page is only reached once logged in
    val session = sessions.get<UserSession>()
    val user = transaction(DatabaseFactory.db) {
        findUser(session!!.email)
    }

    if (user!!.firstName == null) {
        respondRedirect("/user-info")
    } else {
        respondTemplate("manage.peb", model = mapOf(
            "active_nav" to "manage",
            "logged_in" to true
        ))
    }
}

private suspend fun ApplicationCall.displayAbout(loggedIn: Boolean) {
    respondTemplate("about-us.peb", model = mapOf(
        "active_nav" to "about",
        "logged_in" to loggedIn
    ))
}
