package com.flight.server

import com.flight.db.DatabaseFactory
import com.flight.db.TestDatabase
import com.flight.server.auth.configureAuthentication
import com.flight.server.auth.configureSessions
import com.flight.server.routes.configureErrorHandling
import com.flight.server.routes.configureRouting
import com.flight.server.utils.configureTemplates
import io.ktor.server.application.Application
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain
        .main(args)
}

fun Application.module() {
    TransactionManager.defaultDatabase = DatabaseFactory.db
    configureAuthentication()
    configureSessions()
    configureErrorHandling()
    configureRouting()
    configureTemplates()
}

// Configuration for tests
fun Application.testModule() {
    TestDatabase.create()
    TransactionManager.defaultDatabase = TestDatabase.db
    configureAuthentication()
    configureSessions()
    configureRouting()
    configureTemplates()
}
