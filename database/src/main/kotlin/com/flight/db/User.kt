package com.flight.db

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass

class User(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<User>(UserTable)

    var email by UserTable.email
    var passwordHash by UserTable.passwordHash
    var firstName by UserTable.firstName
    var lastName by UserTable.lastName
    var phoneNo by UserTable.phoneNo

    // "firstName lastName"
    val properName: String get() = "$firstName $lastName"

    override fun toString() = properName
}
