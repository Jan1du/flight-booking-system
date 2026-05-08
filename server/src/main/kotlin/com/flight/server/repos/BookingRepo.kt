package com.flight.server.repos

import com.flight.db.Booking
import com.flight.db.BookingTable
import com.flight.db.User
import org.jetbrains.exposed.v1.core.eq

// Returns upcoming (non-completed) bookings for a user sorted by flight date ascending (closest first)
fun findUpcomingBookingsByUser(user: User): List<Booking> =
    Booking
        .find { BookingTable.user eq user.id }
        .filter { it.status != "Completed" }
        .sortedBy { it.flight.date }

// Returns completed bookings for a user sorted by flight date descending (latest first)
fun findCompletedBookingsByUser(user: User): List<Booking> =
    Booking
        .find { BookingTable.user eq user.id }
        .filter { it.status == "Completed" }
        .sortedByDescending { it.flight.date }

// Returns the booking with the given id or null if it does not exist
fun findBookingById(id: Int): Booking? = Booking.findById(id)
