package com.flight.server.routes

import com.flight.server.auth.UserSession
import com.flight.server.repos.findUser
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.authenticate
import io.ktor.server.http.content.staticResources
import io.ktor.server.pebble.respondTemplate
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction

fun Application.configureRouting() {
    routing {
        staticResources("/static", "")
        get("/") { call.displaySearchForm() }
        get("/search") { call.displayResults() }
        get("/search-return") { call.displayReturn() }
        authenticate("auth-session") {
            get("/manage") { call.displayManage() }
            get("/user-info") { call.userInfoPage() }
            post("/user-info") { call.addUserInfo() }
            get("/logout") { call.logout() }
        }
        get("/about") { call.displayAbout() }
        get("/register") { call.registerPage() }
        post("/register") { call.registerUser() }
        get("/login") { call.loginPage() }
        authenticate("auth-form") {
            post("/login") { call.loginUser() }
        }
    }
}

fun ApplicationCall.isLoggedIn() = sessions.get<UserSession>() != null

private suspend fun ApplicationCall.displayManage() {
    suspendTransaction {
        // user and session should always be non-null since the manage page is only reached once logged in
        val session = sessions.get<UserSession>()
        val user = findUser(session!!.email)

        if (user!!.firstName == null) {
            respondRedirect("/user-info")
        } else {
            respondTemplate(
                "manage.peb",
                model =
                    mapOf(
                        "active_nav" to "manage",
                        "logged_in" to true,
                    ),
            )
        }
    }
}

private suspend fun ApplicationCall.displayAbout() {
    respondTemplate(
        "about-us.peb",
        model =
            mapOf(
                "active_nav" to "about",
                "logged_in" to isLoggedIn(),
            ),
    )
}
