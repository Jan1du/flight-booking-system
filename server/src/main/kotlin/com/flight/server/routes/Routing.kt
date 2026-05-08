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
            get("/payment") { call.displayPaymentForm() }
            post("/payment") { call.addBooking() }
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
