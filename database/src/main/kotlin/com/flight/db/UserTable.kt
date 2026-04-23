package com.flight.db

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

const val MAX_VARCHAR_LENGTH = 256
const val MAX_USERNAME_LENGTH = 100
const val MAX_PHONE_LENGTH = 20

object UserTable : IntIdTable("users") {
    // Email is unique
    val email = varchar("email", MAX_VARCHAR_LENGTH).uniqueIndex()

    // Nullable user information
    val firstName = varchar("first_name", MAX_USERNAME_LENGTH).nullable()
    val lastName = varchar("last_name", MAX_USERNAME_LENGTH).nullable()
    val phoneNo = varchar("phone_no", MAX_PHONE_LENGTH).nullable()

    val passwordHash = varchar("hash", MAX_VARCHAR_LENGTH)
}
