package com.flight.server

import com.flight.db.TestDatabase
import com.flight.server.repos.findUser
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.formUrlEncode
import io.ktor.server.testing.testApplication
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.test.assertEquals

@Suppress("unused")
class ApplicationTest :
    StringSpec({
        "Login and registration pages loads properly" {
            testApplication {
                application { testModule() }
                val loginResponse = client.get("/login").also { checkForHtml(it) }
                val registerResponse = client.get("/register").also { checkForHtml(it) }
                loginResponse.bodyAsText() shouldContain "<form action=\"/login\" method=\"POST\">"
                registerResponse.bodyAsText() shouldContain "<form action=\"/register\" method=\"POST\">"
            }
        }

        // Empty input fields are not necessary to test for login and register since the frontend checks for it

        // Login tests
        "A user who is not logged in cannot access the manage page - will be redirected to login page" {
            testApplication {
                application { testModule() }
                val response = client.get("/manage")
                response.bodyAsText() shouldContain "<form action=\"/login\" method=\"POST\">"
            }
        }

        "A user who is not logged in cannot access the user-info page - will be redirected to login page" {
            testApplication {
                application { testModule() }
                val response = client.get("/user-info")
                response.bodyAsText() shouldContain "<form action=\"/login\" method=\"POST\">"
            }
        }

        "A user who has not logged in will see the option to login or register on the navbar" {
            testApplication {
                application { testModule() }
                val response = client.get("/").also { checkForHtml(it) }
                response.bodyAsText() shouldContain "href=\"/login\""
                response.bodyAsText() shouldContain "href=\"/register\""
                response.bodyAsText() shouldNotContain "My Account"
            }
        }

        "A user who already completed his profile gets redirected to the manage page upon successful login" {
            testApplication {
                application { testModule() }
                val response =
                    client.post("/login") {
                        header(
                            HttpHeaders.ContentType,
                            ContentType.Application.FormUrlEncoded.toString(),
                        )
                        setBody(
                            listOf(
                                "email" to "johndoe@gmail.com",
                                "password" to "Password123",
                            ).formUrlEncode(),
                        )
                    }

                assertEquals(HttpStatusCode.Found, response.status)
                assertEquals("/manage", response.headers["Location"])
            }
        }

        """
        A user who hasn't completed his profile gets redirected to the
         user-info page upon trying to access the manage page
        """ {
            // Login will redirect to manage page, which will in turn redirect to user-info
            testApplication {
                application { testModule() }
                val client = createClient { install(HttpCookies) }
                client.post("/login") {
                    header(
                        HttpHeaders.ContentType,
                        ContentType.Application.FormUrlEncoded.toString(),
                    )
                    setBody(
                        listOf(
                            "email" to "peter1973@gmail.com",
                            "password" to "SpiderMan_13",
                        ).formUrlEncode(),
                    )
                }

                val response = client.get("/manage")
                response.bodyAsText() shouldContain "<form action=\"/user-info\" method=\"POST\" id=\"profile-form\">"
            }
        }

        "A user who has logged in can see the My Account button on the navbar" {
            testApplication {
                application { testModule() }
                val client = createClient { install(HttpCookies) }
                client.post("/login") {
                    header(
                        HttpHeaders.ContentType,
                        ContentType.Application.FormUrlEncoded.toString(),
                    )
                    setBody(
                        listOf(
                            "email" to "johndoe@gmail.com",
                            "password" to "Password123",
                        ).formUrlEncode(),
                    )
                }

                val response = client.get("/").also { checkForHtml(it) }
                response.bodyAsText() shouldNotContain "href=\"/login\""
                response.bodyAsText() shouldNotContain "href=\"/register\""
                response.bodyAsText() shouldContain "My Account"
            }
        }

        "A user who has logged in and completed the profile can access the manage page" {
            testApplication {
                application { testModule() }
                val client = createClient { install(HttpCookies) }
                client.post("/login") {
                    header(
                        HttpHeaders.ContentType,
                        ContentType.Application.FormUrlEncoded.toString(),
                    )
                    setBody(
                        listOf(
                            "email" to "johndoe@gmail.com",
                            "password" to "Password123",
                        ).formUrlEncode(),
                    )
                }

                val response = client.get("/manage").also { checkForHtml(it) }
                response.bodyAsText() shouldContain "<a href=\"/manage\" class=\"contrast active-link\">"
            }
        }

        "Whitespaces at either ends of the input email should be ignored (login)" {
            testApplication {
                application { testModule() }
                val response =
                    client.post("/login") {
                        header(
                            HttpHeaders.ContentType,
                            ContentType.Application.FormUrlEncoded.toString(),
                        )
                        setBody(
                            listOf(
                                "email" to "   johndoe@gmail.com      ",
                                "password" to "Password123",
                            ).formUrlEncode(),
                        )
                    }

                assertEquals(HttpStatusCode.Found, response.status)
                assertEquals("/manage", response.headers["Location"])
            }
        }

        "Capital letters in the input email should be ignored (login)" {
            testApplication {
                application { testModule() }
                val response =
                    client.post("/login") {
                        header(
                            HttpHeaders.ContentType,
                            ContentType.Application.FormUrlEncoded.toString(),
                        )
                        setBody(
                            listOf(
                                "email" to "JohnDoe@gmail.com",
                                "password" to "Password123",
                            ).formUrlEncode(),
                        )
                    }

                assertEquals(HttpStatusCode.Found, response.status)
                assertEquals("/manage", response.headers["Location"])
            }
        }

        "Invalid email or password should display an error message" {
            testApplication {
                application { testModule() }
                // Wrong email
                val response1 =
                    client.post("/login") {
                        header(
                            HttpHeaders.ContentType,
                            ContentType.Application.FormUrlEncoded.toString(),
                        )
                        setBody(
                            listOf(
                                "email" to "jondo@gmail.com",
                                "password" to "Password123",
                            ).formUrlEncode(),
                        )
                    }
                response1.bodyAsText() shouldContain "Invalid email or password"

                // Wrong password
                val response2 =
                    client.post("/login") {
                        header(
                            HttpHeaders.ContentType,
                            ContentType.Application.FormUrlEncoded.toString(),
                        )
                        setBody(
                            listOf(
                                "email" to "johndoe@gmail.com",
                                "password" to "password124",
                            ).formUrlEncode(),
                        )
                    }
                response2.bodyAsText() shouldContain "Invalid email or password"
            }
        }

        // Registration tests
        "A user who successfully registers will be redirected to the user-info page" {
            testApplication {
                application { testModule() }
                val response =
                    client.post("/register") {
                        header(
                            HttpHeaders.ContentType,
                            ContentType.Application.FormUrlEncoded.toString(),
                        )
                        setBody(
                            listOf(
                                "email" to "rico96121@gmail.com",
                                "password" to "SeaShells_123",
                                "confirm_password" to "SeaShells_123",
                            ).formUrlEncode(),
                        )
                    }

                assertEquals(HttpStatusCode.Found, response.status)
                assertEquals("/user-info", response.headers["Location"])
            }
        }

        "A user who registers with a new email can login with the registered email" {
            testApplication {
                application { testModule() }
                val client = createClient { install(HttpCookies) }
                client.post("/register") {
                    header(
                        HttpHeaders.ContentType,
                        ContentType.Application.FormUrlEncoded.toString(),
                    )
                    setBody(
                        listOf(
                            "email" to "rico96121@gmail.com",
                            "password" to "SeaShells_123",
                            "confirm_password" to "SeaShells_123",
                        ).formUrlEncode(),
                    )
                }
                client.post("/login") {
                    header(
                        HttpHeaders.ContentType,
                        ContentType.Application.FormUrlEncoded.toString(),
                    )
                    setBody(
                        listOf(
                            "email" to "rico96121@gmail.com",
                            "password" to "SeaShells_123",
                        ).formUrlEncode(),
                    )
                }

                // If login is successful, the user-info page can be accessed
                val response = client.get("/user-info").also { checkForHtml(it) }
                response.bodyAsText() shouldContain "<form action=\"/user-info\" method=\"POST\" id=\"profile-form\">"
            }
        }

        "A user cannot sign up with an email that is already registered" {
            testApplication {
                application { testModule() }
                val response =
                    client.post("/register") {
                        header(
                            HttpHeaders.ContentType,
                            ContentType.Application.FormUrlEncoded.toString(),
                        )
                        setBody(
                            listOf(
                                "email" to "johndoe@gmail.com",
                                "password" to "HelloWorld123",
                                "confirm_password" to "HelloWorld123",
                            ).formUrlEncode(),
                        )
                    }
                response.bodyAsText() shouldContain "Email is already registered"
            }
        }

        "Whitespaces at either ends of the input email should be ignored (register)" {
            testApplication {
                application { testModule() }
                val client = createClient { install(HttpCookies) }
                client.post("/register") {
                    header(
                        HttpHeaders.ContentType,
                        ContentType.Application.FormUrlEncoded.toString(),
                    )
                    setBody(
                        listOf(
                            "email" to "    rico96121@gmail.com  ",
                            "password" to "SeaShells_123",
                            "confirm_password" to "SeaShells_123",
                        ).formUrlEncode(),
                    )
                }
                client.post("/login") {
                    header(
                        HttpHeaders.ContentType,
                        ContentType.Application.FormUrlEncoded.toString(),
                    )
                    setBody(
                        listOf(
                            "email" to "rico96121@gmail.com",
                            "password" to "SeaShells_123",
                        ).formUrlEncode(),
                    )
                }

                // If login is successful, the user-info page can be accessed
                val response = client.get("/user-info").also { checkForHtml(it) }
                response.bodyAsText() shouldContain "<form action=\"/user-info\" method=\"POST\" id=\"profile-form\">"
            }
        }

        "Capital letters in the input email should be ignored (register)" {
            testApplication {
                application { testModule() }
                val client = createClient { install(HttpCookies) }
                client.post("/register") {
                    header(
                        HttpHeaders.ContentType,
                        ContentType.Application.FormUrlEncoded.toString(),
                    )
                    setBody(
                        listOf(
                            "email" to "RiCo96121@gmail.com",
                            "password" to "SeaShells_123",
                            "confirm_password" to "SeaShells_123",
                        ).formUrlEncode(),
                    )
                }
                client.post("/login") {
                    header(
                        HttpHeaders.ContentType,
                        ContentType.Application.FormUrlEncoded.toString(),
                    )
                    setBody(
                        listOf(
                            "email" to "rico96121@gmail.com",
                            "password" to "SeaShells_123",
                        ).formUrlEncode(),
                    )
                }

                // If login is successful, the user-info page can be accessed
                val response = client.get("/user-info").also { checkForHtml(it) }
                response.bodyAsText() shouldContain "<form action=\"/user-info\" method=\"POST\" id=\"profile-form\">"
            }
        }

        "A user cannot sign up if the confirm password does not match the password" {
            testApplication {
                application { testModule() }
                val response =
                    client.post("/register") {
                        header(
                            HttpHeaders.ContentType,
                            ContentType.Application.FormUrlEncoded.toString(),
                        )
                        setBody(
                            listOf(
                                "email" to "rico96121@gmail.com",
                                "password" to "SeaShells_123",
                                "confirm_password" to "SeaShells12",
                            ).formUrlEncode(),
                        )
                    }
                response.bodyAsText() shouldContain "Passwords do not match"
            }
        }

        // Password rules
        "The password must be at least 8 characters long" {
            testApplication {
                application { testModule() }
                // 7 characters
                val response1 =
                    client.post("/register") {
                        header(
                            HttpHeaders.ContentType,
                            ContentType.Application.FormUrlEncoded.toString(),
                        )
                        setBody(
                            listOf(
                                "email" to "rico96121@gmail.com",
                                "password" to "Sea1234",
                                "confirm_password" to "Sea1234",
                            ).formUrlEncode(),
                        )
                    }

                // 8 characters
                val response2 =
                    client.post("/register") {
                        header(
                            HttpHeaders.ContentType,
                            ContentType.Application.FormUrlEncoded.toString(),
                        )
                        setBody(
                            listOf(
                                "email" to "rico96121@gmail.com",
                                "password" to "Sea12345",
                                "confirm_password" to "Sea12345",
                            ).formUrlEncode(),
                        )
                    }

                // 9 characters
                val response3 =
                    client.post("/register") {
                        header(
                            HttpHeaders.ContentType,
                            ContentType.Application.FormUrlEncoded.toString(),
                        )
                        setBody(
                            // different email
                            listOf(
                                "email" to "ricol96121@gmail.com",
                                "password" to "Sea_12345",
                                "confirm_password" to "Sea_12345",
                            ).formUrlEncode(),
                        )
                    }

                response1.bodyAsText() shouldContain "Must be at least 8 characters long"
                assertEquals("/user-info", response2.headers["Location"])
                assertEquals("/user-info", response3.headers["Location"])
            }
        }

        "The password must contain at least one uppercase and lowercase letter" {
            testApplication {
                application { testModule() }
                // no uppercase
                val response1 =
                    client.post("/register") {
                        header(
                            HttpHeaders.ContentType,
                            ContentType.Application.FormUrlEncoded.toString(),
                        )
                        setBody(
                            listOf(
                                "email" to "rico96121@gmail.com",
                                "password" to "seashells123",
                                "confirm_password" to "seashells123",
                            ).formUrlEncode(),
                        )
                    }

                // no lowercase
                val response2 =
                    client.post("/register") {
                        header(
                            HttpHeaders.ContentType,
                            ContentType.Application.FormUrlEncoded.toString(),
                        )
                        setBody(
                            listOf(
                                "email" to "rico96121@gmail.com",
                                "password" to "SEASHELLS123",
                                "confirm_password" to "SEASHELLS123",
                            ).formUrlEncode(),
                        )
                    }

                // no letters
                val response3 =
                    client.post("/register") {
                        header(
                            HttpHeaders.ContentType,
                            ContentType.Application.FormUrlEncoded.toString(),
                        )
                        setBody(
                            listOf(
                                "email" to "rico96121@gmail.com",
                                "password" to "12345678",
                                "confirm_password" to "12345678",
                            ).formUrlEncode(),
                        )
                    }

                response1.bodyAsText() shouldContain "Must contain at least one uppercase and lowercase letter"
                response2.bodyAsText() shouldContain "Must contain at least one uppercase and lowercase letter"
                response3.bodyAsText() shouldContain "Must contain at least one uppercase and lowercase letter"
            }
        }

        "The password must contain at least one number" {
            testApplication {
                application { testModule() }
                val response1 =
                    client.post("/register") {
                        header(
                            HttpHeaders.ContentType,
                            ContentType.Application.FormUrlEncoded.toString(),
                        )
                        setBody(
                            listOf(
                                "email" to "rico96121@gmail.com",
                                "password" to "SeaShells",
                                "confirm_password" to "SeaShells",
                            ).formUrlEncode(),
                        )
                    }

                response1.bodyAsText() shouldContain "Must contain at least one number"
            }
        }

        // User info
        "A logged user who has not completed his profile can use the user-info page" {
            testApplication {
                application { testModule() }
                val client = createClient { install(HttpCookies) }
                client.post("/login") {
                    header(
                        HttpHeaders.ContentType,
                        ContentType.Application.FormUrlEncoded.toString(),
                    )
                    setBody(
                        listOf(
                            "email" to "peter1973@gmail.com",
                            "password" to "SpiderMan_13",
                        ).formUrlEncode(),
                    )
                }

                client.get("/user-info").also { checkForHtml(it) }
                val response =
                    client.post("/user-info") {
                        header(
                            HttpHeaders.ContentType,
                            ContentType.Application.FormUrlEncoded.toString(),
                        )
                        setBody(
                            listOf(
                                "first_name" to "Peter",
                                "last_name" to "Parker",
                                "phone_no" to "+447800000000",
                            ).formUrlEncode(),
                        )
                    }

                assertEquals(HttpStatusCode.Found, response.status)
                assertEquals("/manage", response.headers["Location"])
                transaction(TestDatabase.db) {
                    val user = findUser("peter1973@gmail.com")
                    user?.firstName shouldBe "Peter"
                    user?.lastName shouldBe "Parker"
                    user?.phoneNo shouldBe "+447800000000"
                }
            }
        }

        "A logged user who has completed his profile cannot use the user-info page" {
            testApplication {
                application { testModule() }
                val client = createClient { install(HttpCookies) }
                client.post("/login") {
                    header(
                        HttpHeaders.ContentType,
                        ContentType.Application.FormUrlEncoded.toString(),
                    )
                    setBody(
                        listOf(
                            "email" to "johndoe@gmail.com",
                            "password" to "Password123",
                        ).formUrlEncode(),
                    )
                }

                val response = client.get("/user-info")
                assertEquals(HttpStatusCode.OK, response.status)
                response.bodyAsText() shouldContain "<a href=\"/manage\" class=\"contrast active-link\">"
            }
        }

        // Sign out test
        "A logged user can logout" {
            testApplication {
                application { testModule() }
                val client = createClient { install(HttpCookies) }
                client.post("/login") {
                    header(
                        HttpHeaders.ContentType,
                        ContentType.Application.FormUrlEncoded.toString(),
                    )
                    setBody(
                        listOf(
                            "email" to "johndoe@gmail.com",
                            "password" to "Password123",
                        ).formUrlEncode(),
                    )
                }
                client.get("/logout")
                val response = client.get("/")
                response.bodyAsText() shouldContain "href=\"/login\""
                response.bodyAsText() shouldContain "href=\"/register\""
                response.bodyAsText() shouldNotContain "My Account"
            }
        }
    })

fun checkForHtml(response: HttpResponse) {
    response.status shouldBe HttpStatusCode.OK
    response.headers["Content-Type"]?.shouldContain("text/html")
}
