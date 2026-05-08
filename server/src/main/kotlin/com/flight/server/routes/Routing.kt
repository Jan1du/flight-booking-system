package com.flight.server.routes

import com.flight.server.auth.UserSession
import com.flight.server.repos.findBookingById
import com.flight.server.repos.findCompletedBookingsByUser
import com.flight.server.repos.findUpcomingBookingsByUser
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
            get("/passenger-info") { call.passengerDetailForm() }
            post("/passenger-info") { call.passengerDetails() }
            get("/seat-selection") { call.displaySeatSelection() }
            post("/seat-selection") { call.seatInformation() }
            get("/manage") { call.displayManage() }
            get("/manage/{id}") { call.displayBookingActions() }
            get("/user-info") { call.userInfoPage() }
            post("/user-info") { call.addUserInfo() }
            get("/logout") { call.logout() }
        }
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
            val upcomingBookings = findUpcomingBookingsByUser(user)
            val completedBookings = findCompletedBookingsByUser(user)
            respondTemplate(
                "manage.peb",
                model =
                    mapOf(
                        "active_nav" to "manage",
                        "logged_in" to true,
                        "upcomingBookings" to upcomingBookings,
                        "completedBookings" to completedBookings,
                    ),
            )
        }
    }
}

private suspend fun ApplicationCall.displayBookingActions() {
    suspendTransaction {
        val session = sessions.get<UserSession>()
        val user = findUser(session!!.email)
        val bookingId = parameters["id"]?.toIntOrNull()

        if (bookingId == null) {
            respondRedirect("/manage")
            return@suspendTransaction
        }

        val booking = findBookingById(bookingId)

        // Redirect if booking not found, does not belong to the logged-in user, or is already completed
        if (booking == null || booking.user.id != user!!.id || booking.status == "Completed") {
            respondRedirect("/manage")
            return@suspendTransaction
        }

        respondTemplate(
            "booking-actions.peb",
            model =
                mapOf(
                    "active_nav" to "manage",
                    "logged_in" to true,
                    "booking" to booking,
                ),
        )
    }
}
