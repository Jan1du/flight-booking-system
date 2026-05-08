package com.flight.server.routes

import com.flight.server.auth.BookingSession
import io.ktor.server.application.ApplicationCall
import io.ktor.server.pebble.respondTemplate
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respondRedirect
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import io.ktor.server.util.getOrFail
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import java.time.LocalDate

const val MAX_CHILD_AGE: Long = 18

suspend fun ApplicationCall.passengerDetailForm() {
    suspendTransaction {
        val formParams = request.queryParameters
        val outboundFlightId = formParams.getOrFail("outbound").toInt()
        val returnFlightId = formParams["return"]?.toIntOrNull() ?: -1

        val currentSession = sessions.get<BookingSession>()
        if (currentSession == null) {
            respondRedirect("/search")
        }

        // Non-null (!!) is allowed since the function will redirect to /search if the currentSession is null
        val updatedSession =
            currentSession!!.copy(
                flightId = outboundFlightId,
                returnFlightId = returnFlightId,
            )
        sessions.set(updatedSession)

        val numAdults = updatedSession.numAdults ?: 1
        val numChildren = updatedSession.numChildren ?: 0
        val today = LocalDate.now()
        val validChildDate = today.minusYears(MAX_CHILD_AGE)

        println("today: $today, validChildDate: $validChildDate")

        respondTemplate(
            "passenger-info.peb",
            model =
                mapOf(
                    "active_nav" to "book",
                    "logged_in" to isLoggedIn(),
                    "numAdults" to numAdults,
                    "numChildren" to numChildren,
                    "today" to today.toString(),
                    "validChildDate" to validChildDate.toString(),
                ),
        )
    }
}

suspend fun ApplicationCall.passengerDetails() {
    suspendTransaction {
        val session = sessions.get<BookingSession>()
        val formParameters = receiveParameters()
        if (session == null || session.numAdults == null || session.numChildren == null) {
            respondRedirect("/search")
        }

        val adultFirstNames = mutableListOf<String>()
        val adultLastNames = mutableListOf<String>()
        val childrenFirstNames = mutableListOf<String>()
        val childrenLastNames = mutableListOf<String>()

        // Not-null assertion allowed since session, numAdults and numChildren are checked beforehand
        for (i in 1..session!!.numAdults!!) {
            val adultFirstName = formParameters["adultFirstName_$i"]
            val adultLastName = formParameters["adultLastName_$i"]
            if (!adultFirstName.isNullOrBlank() && !adultLastName.isNullOrBlank()) {
                adultFirstNames.add(adultFirstName)
                adultLastNames.add(adultLastName)
            }
        }

        for (i in 1..session.numChildren!!) {
            val childFirstName = formParameters["childFirstName_$i"]
            val childLastName = formParameters["childLastName_$i"]
            if (!childFirstName.isNullOrBlank() && !childLastName.isNullOrBlank()) {
                childrenFirstNames.add(childFirstName)
                childrenLastNames.add(childLastName)
            }
        }

        sessions.set(
            session.copy(
                passengerFirstNamesAdult = adultFirstNames.toList(),
                passengerLastNamesAdult = adultLastNames.toList(),
                passengerFirstNamesChild = childrenFirstNames.toList(),
                passengerLastNamesChild = childrenLastNames.toList(),
            ),
        )
        respondRedirect("/seat-selection")
    }
}

suspend fun ApplicationCall.displaySeatSelection() {
    val session = sessions.get<BookingSession>()
    if (session == null || session.numAdults == null || session.numChildren == null) {
        respondRedirect("/search")
    }

    // Not-null assertion allowed since session, numAdults and numChildren are checked beforehand
    val totalPassengers = session!!.numAdults!! + session.numChildren!!
    val isReturn = session.returnFlightId != -1

    respondTemplate(
        "seat-selection.peb",
        model =
            mapOf(
                "active_nav" to "book",
                "logged_in" to isLoggedIn(),
                "totalPassengerCount" to totalPassengers,
                "isReturn" to isReturn,
            ),
    )
}

suspend fun ApplicationCall.seatInformation() {
    val session = sessions.get<BookingSession>()
    val formParams = receiveParameters()
    if (session == null) {
        respondRedirect("/search")
    }

    val outboundString = formParams["outboundSeats"] ?: ""
    val returnString = formParams["returnSeats"] ?: ""

    val outboundSeatsList = outboundString.split(",").filter { it.isNotBlank() }
    val returnSeatsList = returnString.split(",").filter { it.isNotBlank() }

    // Not-null assertion allowed since session is checked beforehand
    val updatedSession =
        session!!.copy(
            selectedSeats = outboundSeatsList,
            selectedSeatsReturn = returnSeatsList,
        )
    sessions.set(updatedSession)
    respondRedirect("/payment")
}
