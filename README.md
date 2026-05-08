# Flight-Booking-System

- A simple flight booking website that allows users to search for a flight from one airport to another.
- Implemented using the ktor framework and the Gradle build system
- The system is for demo purposes only as it has limited data on airports and airlines and 
generates random flight data using the airport and airline data.

## Running the Application:

- If the database is not initialised (flight_db.mv.db doesn't exist) or the database needs to be reset:\
    enter `./gradlew :create:run` on the terminal in the root directory

- To run the application:\
    enter `./gradlew :server:run` on the terminal in the root directory

- To run the unit tests:\
    enter `./gradlew check`