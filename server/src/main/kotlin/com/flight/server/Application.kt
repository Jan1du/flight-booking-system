package com.flight.server

import com.flight.db.DatabaseFactory
import io.ktor.server.application.*
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    TransactionManager.defaultDatabase = DatabaseFactory.db
    configureAuthentication()
    configureSessions()
    configureRouting()
    configureTemplates()
}
