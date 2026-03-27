// Update the users.csv file for backup when running the task
// Run using ./gradlew :database:SyncUsersCsv

package com.flight.db

import org.apache.commons.csv.CSVFormat
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.io.File
import java.io.FileWriter

fun main() {
    val usersCsv = "csv/users.csv"
    syncUsersCsv(usersCsv)
}

fun syncUsersCsv(path: String) {
    val csvFile = File(path)
    csvFile.parentFile?.mkdirs()

    transaction(DatabaseFactory.db) {
        val users = User.all().toList()

        FileWriter(csvFile, false).use { writer ->
            writer.write("email,first_name,last_name,phone_no,hash\n")

            val csvPrinter = CSVFormat.DEFAULT.print(writer)

            users.forEach { user ->
                csvPrinter.printRecord(
                    user.email,
                    user.firstName.orEmpty(),
                    user.lastName.orEmpty(),
                    user.phoneNo.orEmpty(),
                    user.passwordHash,
                )
            }

            csvPrinter.flush()
        }
    }
}
