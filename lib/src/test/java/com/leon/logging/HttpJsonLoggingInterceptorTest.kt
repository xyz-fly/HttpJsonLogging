package com.leon.logging

import com.google.gson.GsonBuilder
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test

class HttpJsonLoggingInterceptorTest {

    private val url = "http://com.test".toHttpUrl()
    private val request = Request.Builder().url(url).build()

    private class LogRecorder : HttpJsonLoggingInterceptor.Logger {
        private val log = mutableListOf<String>()

        private var index = 0

        override fun log(message: String) {
            log += message
        }

        fun assertEqual(expected: String) {
            assertEquals(expected, log[index++])
        }
    }

    private class Student(
        val name: String?,
        val age: Int,
    )

    private class School(
        val name: String?,
        val list: List<Student>?
    )

    private fun createResponse(json: String) =
        Response.Builder()
            .code(200)
            .message("OK")
            .protocol(Protocol.HTTP_1_1)
            .request(request)
            .body(json.toResponseBody())
            .build()

    private fun testJson(json: String) {
        val logger = LogRecorder()
        val client = OkHttpClient.Builder()
            .addInterceptor(HttpJsonLoggingInterceptor(logger))
            .addInterceptor(
                Interceptor {
                    createResponse(json)
                }
            )
            .build()
        client.newCall(request).execute() // we want this call to throw a SocketTimeoutException

        val expected = "--> JSON <--\n$json\n--> JSON END <--"
        logger.assertEqual(expected)
        println(expected)
    }

    @Test
    fun testSchool4() {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val json = gson.toJson(School("xyz", listOf(Student("abc", 10), Student(null, 10))))
        testJson(json)
    }

    @Test
    fun testSchool3() {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val json = gson.toJson(School("xyz", emptyList()))
        testJson(json)
    }

    @Test
    fun testSchool2() {
        val gson = GsonBuilder().setPrettyPrinting().serializeNulls().create()
        val json = gson.toJson(School("xyz", null))
        testJson(json)
    }

    @Test
    fun testSchool1() {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val json = gson.toJson(School("xyz", null))
        testJson(json)
    }

    @Test
    fun testStudent5() {
        val gson = GsonBuilder().setPrettyPrinting().serializeNulls().create()
        val json = gson.toJson(listOf(Student("abc", 10), Student(null, 10)))
        testJson(json)
    }

    @Test
    fun testStudent4() {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val json = gson.toJson(listOf(Student("abc", 10), Student(null, 10)))
        testJson(json)
    }

    @Test
    fun testStudent3() {
        val gson = GsonBuilder().setPrettyPrinting().serializeNulls().create()
        val json = gson.toJson(Student(null, 10))
        testJson(json)
    }

    @Test
    fun testStudent2() {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val json = gson.toJson(Student(null, 10))
        testJson(json)
    }

    @Test
    fun testStudent1() {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val json = gson.toJson(Student("abc", 10))
        testJson(json)
    }
}
