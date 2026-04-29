package com.flight.server.repos

import com.flight.db.User
import com.flight.db.UserTable
import com.password4j.Password
import io.ktor.server.auth.UserPasswordCredential
import org.jetbrains.exposed.v1.core.eq

const val MIN_PASSWORD_LENGTH = 8
const val SALT_LENGTH = 16

// Password rules
fun UserPasswordCredential.passwordIsValid() =
    when {
        (password.length < MIN_PASSWORD_LENGTH) -> false
        (password.filter { it.isLetter() }.firstOrNull { it.isUpperCase() } == null) -> false
        (password.filter { it.isLetter() }.firstOrNull { it.isLowerCase() } == null) -> false
        (password.firstOrNull { it.isDigit() } == null) -> false
        (password.any { it.isWhitespace() }) -> false
        else -> true
    }

// Returns the user with the given email or Null if the user doesn't exist
fun findUser(email: String): User? = User.find { UserTable.email eq email }.firstOrNull()

// Checks if the password matches
fun checkPass(cred: UserPasswordCredential): Boolean {
    val user = findUser(cred.name.lowercase().trim())
    if (user == null) {
        return false
    }

    return Password.check(cred.password, user.passwordHash).withScrypt()
}

// Adds a user to the database
fun addUser(cred: UserPasswordCredential) {
    require(findUser(cred.name) == null) { "email-error" }
    require(cred.passwordIsValid()) { "password-error" }

    val hash = Password.hash(cred.password).addRandomSalt(SALT_LENGTH).withScrypt()

    User.new {
        email = cred.name
        passwordHash = hash.result
    }
}

// Adding other user info to the database
fun updateFirstName(
    email: String,
    newFirstName: String,
) {
    // User is guaranteed to be not null since they are already logged in
    val user = findUser(email)
    user!!.firstName = newFirstName
}

fun updateLastName(
    email: String,
    newLastName: String,
) {
    val user = findUser(email)
    user!!.lastName = newLastName
}

fun updatePhone(
    email: String,
    newPhone: String,
) {
    val user = findUser(email)
    user!!.phoneNo = newPhone
}
