package com.flight.server.routes

import com.flight.server.auth.UserSession
import com.flight.server.repos.findUser
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.authenticate
import io.ktor.server.pebble.respondTemplate
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import io.ktor.http.Parameters

fun Application.configureRouting() {
    routing {
        get("/") { call.displaySearchForm() }
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
        get("/search") { call.displayResults() }
    }
}

fun ApplicationCall.isLoggedIn() = sessions.get<UserSession>() != null

private suspend fun ApplicationCall.displaySearchForm() {
    respondTemplate(
        "search-form.peb",
        model =
            mapOf(
                "active_nav" to "book",
                "logged_in" to isLoggedIn(),
            ),
    )
}

private suspend fun ApplicationCall.displayResults() {
    val queryParameters: Parameters = request.queryParameters

    val origin = queryParameters["origin"]?.trim().orEmpty()
    val destination = queryParameters["destination"]?.trim().orEmpty()
    val departDate = queryParameters["depart_date"]?.trim().orEmpty()
    val returnDate = queryParameters["return_date"]?.trim().orEmpty()
    val cabinClass = queryParameters["cabin_class"]?.trim().orEmpty()

    respondTemplate(
        "flight-results.peb",
        model =
            mapOf(
                "active_nav" to "book",
                "logged_in" to isLoggedIn(),
                "origin" to origin,
                "destination" to destination,
                "depart_date" to departDate,
                "return_date" to returnDate,
                "cabin_class" to cabinClass,
                "flights" to emptyList<Map<String, String>>(),
            ),
    )
}

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
