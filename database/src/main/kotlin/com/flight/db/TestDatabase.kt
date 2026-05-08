package com.flight.db

import com.password4j.Password
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.math.BigDecimal

object TestDatabase {
    const val URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;"
    const val DRIVER = "org.h2.Driver"

    val db by lazy {
        Database.connect(URL, DRIVER)
    }

    fun create() {
        transaction(db) {
            SchemaUtils.drop(TicketTable, BookingTable, FlightTable, AirlineTable, AirportTable, UserTable)
            SchemaUtils.create(UserTable, AirlineTable, AirportTable, FlightTable, BookingTable, TicketTable)

            val passwordJohn = Password.hash("Password123").addRandomSalt(16).withScrypt()
            val passwordPeter = Password.hash("SpiderMan_13").addRandomSalt(16).withScrypt()
            val passwordAlice = Password.hash("AliceTest_01").addRandomSalt(16).withScrypt()

            val johnId =
                UserTable.insertAndGetId {
                    it[email] = "johndoe@gmail.com"
                    it[firstName] = "John"
                    it[lastName] = "Doe"
                    it[phoneNo] = "+447890123456"
                    it[passwordHash] = passwordJohn.result
                }

            val peterId =
                UserTable.insertAndGetId {
                    it[email] = "peter1973@gmail.com"
                    it[passwordHash] = passwordPeter.result
                }

            val aliceId =
                UserTable.insertAndGetId {
                    it[email] = "alice@gmail.com"
                    it[firstName] = "Alice"
                    it[lastName] = "Wonderland"
                    it[phoneNo] = "+441234567890"
                    it[passwordHash] = passwordAlice.result
                }

            val skyJetId =
                AirlineTable.insertAndGetId {
                    it[name] = "SkyJet"
                }

            val euroAirId =
                AirlineTable.insertAndGetId {
                    it[name] = "EuroAir"
                }

            val blueWingsId =
                AirlineTable.insertAndGetId {
                    it[name] = "BlueWings"
                }

            val heathrowId =
                AirportTable.insertAndGetId {
                    it[name] = "Heathrow"
                    it[city] = "London"
                    it[country] = "United Kingdom"
                }

            val parisId =
                AirportTable.insertAndGetId {
                    it[name] = "Charles de Gaulle"
                    it[city] = "Paris"
                    it[country] = "France"
                }

            val romeId =
                AirportTable.insertAndGetId {
                    it[name] = "Leonardo da Vinci"
                    it[city] = "Rome"
                    it[country] = "Italy"
                }

            val madridId =
                AirportTable.insertAndGetId {
                    it[name] = "Barajas"
                    it[city] = "Madrid"
                    it[country] = "Spain"
                }

            val skyjetParisFlightId =
                FlightTable.insertAndGetId {
                    it[airline] = skyJetId
                    it[departureAirport] = heathrowId
                    it[arrivalAirport] = parisId
                    it[date] = "2026-05-03"
                    it[departureTime] = "08:30"
                    it[arrivalTime] = "10:50"
                    it[defaultPrice] = BigDecimal("120.00")
                    it[status] = "Scheduled"
                }

            val euroAirRomeFlightId =
                FlightTable.insertAndGetId {
                    it[airline] = euroAirId
                    it[departureAirport] = heathrowId
                    it[arrivalAirport] = romeId
                    it[date] = "2026-03-24"
                    it[departureTime] = "13:15"
                    it[arrivalTime] = "16:25"
                    it[defaultPrice] = BigDecimal("185.00")
                    it[status] = "Scheduled"
                }

            val blueWingsMadridFlightId =
                FlightTable.insertAndGetId {
                    it[airline] = blueWingsId
                    it[departureAirport] = heathrowId
                    it[arrivalAirport] = madridId
                    it[date] = "2026-05-15"
                    it[departureTime] = "09:40"
                    it[arrivalTime] = "13:05"
                    it[defaultPrice] = BigDecimal("320.00")
                    it[status] = "Scheduled"
                }

            val skyJetMadridFlightId =
                FlightTable.insertAndGetId {
                    it[airline] = skyJetId
                    it[departureAirport] = heathrowId
                    it[arrivalAirport] = madridId
                    it[date] = "2026-09-10"
                    it[departureTime] = "07:15"
                    it[arrivalTime] = "10:30"
                    it[defaultPrice] = BigDecimal("210.00")
                    it[status] = "Scheduled"
                }

            val euroAirParisFlightId =
                FlightTable.insertAndGetId {
                    it[airline] = euroAirId
                    it[departureAirport] = heathrowId
                    it[arrivalAirport] = parisId
                    it[date] = "2026-10-20"
                    it[departureTime] = "14:00"
                    it[arrivalTime] = "16:20"
                    it[defaultPrice] = BigDecimal("195.00")
                    it[status] = "Scheduled"
                }

            val upcomingBookingId =
                BookingTable.insertAndGetId {
                    it[user] = johnId
                    it[bookingDate] = "2026-04-20"
                    it[paxCount] = 1
                    it[status] = "Confirmed"
                }

            TicketTable.insert {
                it[booking] = upcomingBookingId
                it[flight] = skyJetMadridFlightId
                it[passengerFirstName] = "John"
                it[passengerLastName] = "Doe"
                it[seatNumber] = null
                it[cabinClass] = "Economy"
                it[ticketPrice] = BigDecimal("210.00")
            }

            val upcomingBooking2Id =
                BookingTable.insertAndGetId {
                    it[user] = johnId
                    it[bookingDate] = "2026-04-25"
                    it[paxCount] = 2
                    it[status] = "Confirmed"
                }

            TicketTable.insert {
                it[booking] = upcomingBooking2Id
                it[flight] = euroAirParisFlightId
                it[passengerFirstName] = "John"
                it[passengerLastName] = "Doe"
                it[seatNumber] = null
                it[cabinClass] = "Business"
                it[ticketPrice] = BigDecimal("195.00")
            }

            val completedBookingId =
                BookingTable.insertAndGetId {
                    it[user] = johnId
                    it[bookingDate] = "2026-01-10"
                    it[paxCount] = 1
                    it[status] = "Completed"
                }

            TicketTable.insert {
                it[booking] = completedBookingId
                it[flight] = euroAirRomeFlightId
                it[passengerFirstName] = "John"
                it[passengerLastName] = "Doe"
                it[seatNumber] = "12A"
                it[cabinClass] = "Business"
                it[ticketPrice] = BigDecimal("185.00")
            }

            val completedBooking2Id =
                BookingTable.insertAndGetId {
                    it[user] = johnId
                    it[bookingDate] = "2026-03-15"
                    it[paxCount] = 1
                    it[status] = "Completed"
                }

            TicketTable.insert {
                it[booking] = completedBooking2Id
                it[flight] = skyjetParisFlightId
                it[passengerFirstName] = "John"
                it[passengerLastName] = "Doe"
                it[seatNumber] = "5C"
                it[cabinClass] = "Economy"
                it[ticketPrice] = BigDecimal("120.00")
            }
        }
    }
}
