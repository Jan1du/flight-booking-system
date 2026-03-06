package com

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.pebble.respondTemplate

fun Application.configureRouting() {
    routing {
        get("/") { call.displaySearchForm() }
        get("/manage") { call.displayManage()}
        get("/about") { call.displayAbout() }
        get("/register") { call.registerPage() }
        get("/login") { call.loginPage() }
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

private suspend fun ApplicationCall.registerPage() {
    respondTemplate("register.peb", model = mapOf("active_nav" to "register"))
}

private suspend fun ApplicationCall.loginPage() {
    respondTemplate("login.peb", model = mapOf("active_nav" to "login"))
}
