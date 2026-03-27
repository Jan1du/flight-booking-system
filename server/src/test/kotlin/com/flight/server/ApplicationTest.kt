package com.flight.server

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

        "Whitespaces at either ends of the input email should be ignored" {
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

        "Capital letters in the input email should be ignored" {
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
    })

fun checkForHtml(response: HttpResponse) {
    response.status shouldBe HttpStatusCode.OK
    response.headers["Content-Type"]?.shouldContain("text/html")
}
