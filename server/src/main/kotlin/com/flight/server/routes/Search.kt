package com.flight.server.routes

import com.flight.server.repos.findFlights
import io.ktor.server.application.ApplicationCall
import io.ktor.server.pebble.respondTemplate
import io.ktor.server.util.getOrFail
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction

suspend fun ApplicationCall.displaySearchForm() {
    respondTemplate(
        "search-form.peb",
        model =
            mapOf(
                "active_nav" to "book",
                "logged_in" to isLoggedIn(),
            ),
    )
}

suspend fun ApplicationCall.displayResults() {
    suspendTransaction {
        val formParams = request.queryParameters

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
