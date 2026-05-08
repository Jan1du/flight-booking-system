package com.flight.db

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

const val MAX_BOOKING_DATE_LENGTH = 10 // yyyy-mm-dd
const val MAX_BOOKING_STATUS_LENGTH = 9 // completed, confirmed, pending 

object BookingTable : IntIdTable("booking") {
    // Foreign keys
    val user = reference("user_id", UserTable, ReferenceOption.CASCADE)

    val bookingDate = varchar("booking_date", MAX_BOOKING_DATE_LENGTH)
    val paxCount = integer("pax_count")
    val status = varchar("status", MAX_BOOKING_STATUS_LENGTH)
}
