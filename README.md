# Flight-Booking-System

- A simple flight booking website that allows users to search for a flight from one airport to another.
- Implemented using the ktor framework and the Gradle build system
- The system is for demo purposes only as it has limited data on airports and airlines and 
generates random flight data using the airport and airline data.
- The **Wiki** with all the documentation is located in the 'docs' folder.

## Running the Application:

- If the database is not initialised (flight_db.mv.db doesn't exist) or the database needs to be reset:\
    enter `./gradlew :create:run` on the terminal in the root directory

- To run the application:\
    enter `./gradlew :server:run` 

- To run the unit tests:\
    enter `./gradlew check`

## Using the Application:

- Flight data is randomly generated for the next 2 months (60 days)
with 10 chosen airports and airlines. So the airport names need to 
be selected from the drop-down, and the dates above 60 days may not have
any flights.

- Users can search outbound and return flights, but cannot proceed to 
booking without registering or logging in.

- Users can look at the flight results in the "Manage" tab once a successful
booking is made