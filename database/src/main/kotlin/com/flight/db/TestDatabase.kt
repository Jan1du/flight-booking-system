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
            SchemaUtils.drop(FlightTable, AirlineTable, AirportTable, UserTable)
            SchemaUtils.create(UserTable, AirlineTable, AirportTable, FlightTable)

            val passwordJohn = Password.hash("Password123").addRandomSalt(16).withScrypt()
            val passwordPeter = Password.hash("SpiderMan_13").addRandomSalt(16).withScrypt()

            UserTable.insert {
                it[email] = "johndoe@gmail.com"
                it[firstName] = "John"
                it[lastName] = "Doe"
                it[phoneNo] = "+447890123456"
                it[passwordHash] = passwordJohn.result
            }

            UserTable.insert {
                it[email] = "peter1973@gmail.com"
                it[passwordHash] = passwordPeter.result
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

            FlightTable.insert {
                it[airline] = skyJetId
                it[departureAirport] = heathrowId
                it[arrivalAirport] = parisId
                it[date] = "2026-05-03"
                it[departureTime] = "08:30"
                it[arrivalTime] = "10:50"
                it[defaultPrice] = BigDecimal("120.00")
                it[status] = "Scheduled"
            }

            FlightTable.insert {
                it[airline] = euroAirId
                it[departureAirport] = heathrowId
                it[arrivalAirport] = romeId
                it[date] = "2026-03-24"
                it[departureTime] = "13:15"
                it[arrivalTime] = "16:25"
                it[defaultPrice] = BigDecimal("185.00")
                it[status] = "Scheduled"
            }

            FlightTable.insert {
                it[airline] = blueWingsId
                it[departureAirport] = heathrowId
                it[arrivalAirport] = madridId
                it[date] = "2026-05-15"
                it[departureTime] = "09:40"
                it[arrivalTime] = "13:05"
                it[defaultPrice] = BigDecimal("320.00")
                it[status] = "Scheduled"
            }
        }
    }
}
