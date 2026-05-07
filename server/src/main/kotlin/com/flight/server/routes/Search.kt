package com.flight.server.routes

import com.flight.db.Airport
import com.flight.db.Flight
import com.flight.server.repos.findFlights
import io.ktor.server.application.ApplicationCall
import io.ktor.server.pebble.respondTemplate
import io.ktor.server.util.getOrFail
import org.jetbrains.exposed.v1.jdbc.SizedIterable
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction

const val DEFAULT_PRICE_MULTI = 1.0
const val BUSINESS_PRICE_MULTI = 1.5
const val FIRST_PRICE_MULTI = 2.0

private fun getFlightInfo(
    origin: String,
    destination: String,
    date: String,
): SizedIterable<Flight> {
    val flights =
        findFlights(
            origin = origin,
            destination = destination,
            date = date,
        )

    return flights
}

private fun getPriceMulti(cabin: String) =
    when (cabin) {
        "economy" -> DEFAULT_PRICE_MULTI
        "business" -> BUSINESS_PRICE_MULTI
        "first" -> FIRST_PRICE_MULTI
        else -> DEFAULT_PRICE_MULTI
    }

suspend fun ApplicationCall.displaySearchForm() {
    suspendTransaction {
        val airports = Airport.all().toList()
        respondTemplate(
            "search-form.peb",
            model =
                mapOf(
                    "active_nav" to "book",
                    "logged_in" to isLoggedIn(),
                    "airports" to airports,
                ),
        )
    }
}

suspend fun ApplicationCall.displayResults() {
    suspendTransaction {
        val formParams = request.queryParameters

        val tripType = formParams["trip_type"] ?: "one-way"
        val origin = formParams.getOrFail("origin")
        val destination = formParams.getOrFail("destination")
        val departDate = formParams.getOrFail("depart_date")
        val returnDate = formParams["return_date"] ?: ""
        val cabinClass = formParams["cabin_class"] ?: "economy"

        val flights = getFlightInfo(origin, destination, departDate)
        val priceMulti = getPriceMulti(cabinClass)

        respondTemplate(
            "flight-results.peb",
            model =
                mapOf(
                    "active_nav" to "book",
                    "logged_in" to isLoggedIn(),
                    "trip_type" to tripType,
                    "origin" to origin,
                    "destination" to destination,
                    "depart_date" to departDate,
                    "return_date" to returnDate,
                    "cabin_class" to cabinClass,
                    "flights" to flights.toList(),
                    "count" to flights.count(),
                    "price_multi" to priceMulti,
                ),
        )
    }
}

suspend fun ApplicationCall.displayReturn() {
    suspendTransaction {
        val formParams = request.queryParameters

        val origin = formParams.getOrFail("origin")
        val destination = formParams.getOrFail("destination")
        val departDate = formParams.getOrFail("depart_date")
        val cabinClass = formParams["cabin_class"] ?: "economy"

        val flights = getFlightInfo(origin, destination, departDate)
        val priceMulti = getPriceMulti(cabinClass)

        respondTemplate(
            "return-results.peb",
            model =
                mapOf(
                    "origin" to origin,
                    "destination" to destination,
                    "depart_date" to departDate,
                    "cabin_class" to cabinClass,
                    "flights" to flights.toList(),
                    "count" to flights.count(),
                    "price_multi" to priceMulti,
                ),
        )
    }
}
