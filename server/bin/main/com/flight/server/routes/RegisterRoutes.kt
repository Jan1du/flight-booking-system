package com.flight.server.routes

import com.flight.server.auth.UserSession
import com.flight.server.repos.addUser
import com.flight.server.repos.findUser
import com.flight.server.repos.updateFirstName
import com.flight.server.repos.updateLastName
import com.flight.server.repos.updatePhone
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
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction

// For formating names
private fun String.formatName(): String =
    this
        .split(" ")
        .joinToString(" ") { word ->
            word.lowercase().replaceFirstChar {
                it.uppercase()
            }
        }

suspend fun ApplicationCall.registerPage() {
    if (isLoggedIn()) {
        respondRedirect("/manage")
    }
    respondTemplate("register.peb", model = mapOf("active_nav" to "register"))
}

suspend fun ApplicationCall.registerUser() {
    suspendTransaction {
        val formParams = receiveParameters()
        val email = formParams.getOrFail("email").lowercase().trim()
        val pass = formParams.getOrFail("password")
        val confirmPass = formParams.getOrFail("confirm_password")

        if (pass == confirmPass) {
            val credentials = UserPasswordCredential(email, pass)
            val result =
                runCatching {
                    addUser(credentials)
                }

            if (result.isSuccess) {
                application.log.info("$email registered successfully")
                sessions.set(UserSession(email))
                respondRedirect("/user-info")
            } else {
                val error = result.exceptionOrNull()?.message ?: ""
                application.log.error("Registration error: $error")
                respondTemplate(
                    "register.peb",
                    model =
                        mapOf(
                            "active_nav" to "register",
                            "error" to error,
                        ),
                )
            }
        } else {
            val error = "password-mismatch"
            respondTemplate(
                "register.peb",
                model =
                    mapOf(
                        "active_nav" to "register",
                        "error" to error,
                    ),
            )
        }
    }
}

suspend fun ApplicationCall.userInfoPage() {
    suspendTransaction {
        val session = sessions.get<UserSession>()
        if (session == null) {
            respondRedirect("/login")
        }
        val user = findUser(session!!.email)
        if (user?.firstName == null) {
            respondTemplate(
                "user-info.peb",
                model = mapOf("logged_in" to true),
            )
        } else {
            respondRedirect("/manage")
        }
    }
}

suspend fun ApplicationCall.addUserInfo() {
    suspendTransaction {
        val formParams = receiveParameters()
        val firstName = formParams.getOrFail("first_name").formatName()
        val lastName = formParams.getOrFail("last_name").formatName()
        val phoneNo = formParams.getOrFail("phone_no")

        val session = sessions.get<UserSession>()
        if (session != null) {
            val email = session.email
            val result =
                runCatching {
                    updateFirstName(email, firstName)
                    updateLastName(email, lastName)
                    updatePhone(email, phoneNo)
                }

            if (result.isSuccess) {
                application.log.info("Profile update successfully")
            } else {
                val error = result.exceptionOrNull()?.message ?: ""
                application.log.error("profile update failed: $error")
                respondRedirect("/user-info")
            }

            respondRedirect("/manage")
        } else {
            respondRedirect("/login")
        }
    }
}
