package com.flight.server.routes

import com.flight.server.auth.UserSession
import com.flight.server.repos.findBookingById
import com.flight.server.repos.findCompletedBookingsByUser
import com.flight.server.repos.findUpcomingBookingsByUser
import com.flight.server.repos.findUser
import io.ktor.server.application.ApplicationCall
import io.ktor.server.pebble.respondTemplate
import io.ktor.server.response.respondRedirect
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import kotlin.text.toIntOrNull

suspend fun ApplicationCall.displayManage() {
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

suspend fun ApplicationCall.displayBookingActions() {
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
