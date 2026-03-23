package com.flight.server

import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.log
import io.ktor.server.auth.UserPasswordCredential
import io.ktor.server.pebble.respondTemplate
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respondRedirect
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import io.ktor.server.util.getOrFail

// For formating names
private fun String.formatName(): String {
    return this.split(" ")
        .joinToString(" ") { word ->
            word.lowercase().replaceFirstChar {
                it.uppercase()
            }
        }
}

suspend fun ApplicationCall.registerPage() {
    respondTemplate("register.peb", model = mapOf("active_nav" to "register"))
}

suspend fun ApplicationCall.registerUser() {
    val formParams = receiveParameters()
    val email = formParams.getOrFail("email").lowercase().trim()
    val pass = formParams.getOrFail("password")
    val confirmPass = formParams.getOrFail("confirm_password")

    if (pass == confirmPass) {
        val credentials = UserPasswordCredential(email, pass)
        val result = runCatching {
            addUser(credentials)
        }

        if (result.isSuccess) {
            application.log.info("$email registered successfully")
            sessions.set(UserSession(email))
            respondRedirect("/user-info")
        } else {
            val error = result.exceptionOrNull()?.message ?: ""
            application.log.error("Registration error: $error")
            respondTemplate("register.peb", model = mapOf(
                "active_nav" to "register",
                "error" to error
            ))
        }
    } else {
        val error = "password-mismatch"
        respondTemplate("register.peb", model = mapOf(
            "active_nav" to "register",
            "error" to error
        ))
    }
}

suspend fun ApplicationCall.userInfoPage() {
    respondTemplate("user-info.peb", model = mapOf(
        "logged_in" to true
        ))
}

suspend fun ApplicationCall.addUserInfo() {
    val formParams = receiveParameters()
    val firstName = formParams.getOrFail("first_name").formatName()
    val lastName = formParams.getOrFail("last_name").formatName()
    val phoneNo = formParams.getOrFail("phone_no")

    val session = sessions.get<UserSession>()
    if (session != null) {
        val email = session.email
        val result = runCatching {
            updateFirstName(email, firstName)
            updateLastName(email, lastName)
            updatePhone(email, phoneNo)
        }

        if (result.isSuccess) {
            application.log.info("Profile update successfully")
        } else {
            val error = result.exceptionOrNull()?.message ?: ""
            application.log.error("profile update failed: $error")
        }
    respondRedirect("/manage")

    } else {
        respondRedirect("/login")
    }
}