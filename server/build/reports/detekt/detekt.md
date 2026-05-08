# detekt

## Metrics

* 112 number of properties

* 34 number of functions

* 2 number of classes

* 5 number of packages

* 12 number of kt files

## Complexity Report

* 1,490 lines of code (loc)

* 1,317 source lines of code (sloc)

* 934 logical lines of code (lloc)

* 36 comment lines of code (cloc)

* 82 cyclomatic complexity (mcc)

* 45 cognitive complexity

* 1 number of total code smells

* 2% comment source ratio

* 87 mcc per 1,000 lloc

* 1 code smells per 1,000 lloc

## Findings (1)

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

generated with [detekt version 1.23.8](https://detekt.dev/) on 2026-05-08 09:11:13 UTC
