package com.flight.server.routes

import com.flight.db.Airline
import com.flight.db.Airport
import com.flight.server.auth.UserSession
import com.flight.server.repos.findFlights
import com.flight.server.repos.findUser
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.authenticate
import io.ktor.server.pebble.respondTemplate
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.util.getOrFail
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction

fun Application.configureRouting() {
    routing {
        get("/") { call.displaySearchForm() }
        post("/") { call.displayResults() }
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
    suspendTransaction {
        val formParams = receiveParameters()

        val origin = formParams.getOrFail("origin")
        val destination = formParams.getOrFail("destination")
        val departDate = formParams.getOrFail("depart_date")
        val returnDate = formParams["return_date"] ?: ""
        val cabinClass = formParams.getOrFail("cabin_class")

        val flights =
            findFlights(
                origin = origin,
                destination = destination,
                date = departDate,
            )
        val count = flights.count()

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
                    "flights" to flights.toList(),
                    "count" to count,
                ),
        )
    }
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
