package com.flight.server

import com.flight.db.DatabaseFactory
import com.flight.db.TestDatabase
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
