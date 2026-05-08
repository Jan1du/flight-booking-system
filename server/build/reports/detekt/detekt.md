# detekt

## Metrics

* 171 number of properties

* 44 number of functions

* 3 number of classes

* 5 number of packages

* 16 number of kt files

## Complexity Report

* 1,945 lines of code (loc)

* 1,710 source lines of code (sloc)

* 1,223 logical lines of code (lloc)

* 47 comment lines of code (cloc)

* 136 cyclomatic complexity (mcc)

* 93 cognitive complexity

* 5 number of total code smells

* 2% comment source ratio

* 111 mcc per 1,000 lloc

* 4 code smells per 1,000 lloc

## Findings (5)

### complexity, ComplexCondition (1)

Complex conditions should be simplified and extracted into well-named methods if necessary.

[Documentation](https://detekt.dev/docs/rules/complexity#complexcondition)

* C:/Users/janid/Documents/MyProjects/flight-booking-system/server/src/main/kotlin/com/flight/server/routes/BookingRoutes.kt:194:13
```
This condition is too complex (6). Defined complexity threshold for conditions is set to '4'
```
```kotlin
191         // Allows non-null assertions for userSession, bookingSession,
192         // flightId, numAdults, numChildren and cabinClass
193         if (
194             bookingSession == null ||
!!!             ^ error
195             userSession == null ||
196             bookingSession.flightId == null ||
197             bookingSession.numAdults == null ||

```

### complexity, CyclomaticComplexMethod (1)

Prefer splitting up complex methods into smaller, easier to test methods.

[Documentation](https://detekt.dev/docs/rules/complexity#cyclomaticcomplexmethod)

* C:/Users/janid/Documents/MyProjects/flight-booking-system/server/src/main/kotlin/com/flight/server/routes/BookingRoutes.kt:186:29
```
The function addBooking appears to be too complex based on Cyclomatic Complexity (complexity: 17). Defined complexity threshold for methods is set to '15'
```
```kotlin
183     )
184 }
185 
186 suspend fun ApplicationCall.addBooking() {
!!!                             ^ error
187     suspendTransaction {
188         val bookingSession = sessions.get<BookingSession>()
189         val userSession = sessions.get<UserSession>()

```

### complexity, LargeClass (1)

One class should have one responsibility. Large classes tend to handle many things at once. Split up large classes into smaller classes that are easier to understand.

[Documentation](https://detekt.dev/docs/rules/complexity#largeclass)

* C:/Users/janid/Documents/MyProjects/flight-booking-system/server/src/test/kotlin/com/flight/server/ApplicationTest.kt:25:7
```
Class ApplicationTest is too large. Consider splitting it into smaller pieces.
```
```kotlin
22 import kotlin.test.assertEquals
23 
24 @Suppress("unused")
25 class ApplicationTest :
!!       ^ error
26     StringSpec({
27         "Login and registration pages loads properly" {
28             testApplication {

```

### complexity, LongMethod (1)

One method should have one responsibility. Long methods tend to handle many things at once. Prefer smaller methods to make them easier to understand.

[Documentation](https://detekt.dev/docs/rules/complexity#longmethod)

* C:/Users/janid/Documents/MyProjects/flight-booking-system/server/src/main/kotlin/com/flight/server/routes/BookingRoutes.kt:186:29
```
The function addBooking is too long (96). The maximum length is 60.
```
```kotlin
183     )
184 }
185 
186 suspend fun ApplicationCall.addBooking() {
!!!                             ^ error
187     suspendTransaction {
188         val bookingSession = sessions.get<BookingSession>()
189         val userSession = sessions.get<UserSession>()

```

### complexity, LongParameterList (1)

The more parameters a function has the more complex it is. Long parameter lists are often used to control complex algorithms and violate the Single Responsibility Principle. Prefer functions with short parameter lists.

[Documentation](https://detekt.dev/docs/rules/complexity#longparameterlist)

* C:/Users/janid/Documents/MyProjects/flight-booking-system/server/src/main/kotlin/com/flight/server/routes/BookingRoutes.kt:25:22
```
The function addTicket(currentBooking: Booking, currentFlight: Flight, firstName: String, lastName: String, cabin: String, seatnumber: String, price: BigDecimal) has too many parameters. The current threshold is set to 6.
```
```kotlin
22 const val BIG_DECIMAL_SCALE: Int = 2
23 const val HALF: Double = 0.5
24 
25 private fun addTicket(
!!                      ^ error
26     currentBooking: Booking,
27     currentFlight: Flight,
28     firstName: String,

```

generated with [detekt version 1.23.8](https://detekt.dev/) on 2026-05-08 13:26:52 UTC
