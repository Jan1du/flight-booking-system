package com.flight.server

import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.*
import io.ktor.server.pebble.respondTemplate

fun Application.configureRouting() {
    routing {
        get("/") { call.displaySearchForm() }
        authenticate("auth-session") {
            get("/manage") { call.displayManage() }
            get("/user-info") { call.userInfoPage() }
            post("/user-info") { call.addUserInfo() }
        }
        get("/about") { call.displayAbout() }
        get("/register") { call.registerPage() }
        post("/register") { call.registerUser() }
        get("/login") { call.loginPage() }
        authenticate("auth-form") {
            post("/login") { call.loginUser() }
        }
        get("/search") { call.displayResults() }
    }
}

private suspend fun ApplicationCall.displaySearchForm() {
    respondTemplate("search-form.peb", model = mapOf("active_nav" to "book"))
}

private suspend fun ApplicationCall.displayResults() {
    respondTemplate("flight-results.peb", model = mapOf("active_nav" to "book"))
}

private suspend fun ApplicationCall.displayManage() {
    respondTemplate("manage.peb", model = mapOf("active_nav" to "manage"))
}

private suspend fun ApplicationCall.displayAbout() {
    respondTemplate("about-us.peb", model = mapOf("active_nav" to "about"))
}
