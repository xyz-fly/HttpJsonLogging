package com.leon.logging

import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.StringReader
import java.io.StringWriter
import java.util.*
import org.junit.Assert
import org.junit.Test

class JsonTest {

    private class Person(
        val name: String,
        val age: Int,
        val join: Boolean,
        val birthday: Long,
        val summary: String?
    )

    private val Joanna = Person("Joanna", 18, false, Calendar.getInstance().apply { set(2000, 0, 1) }.timeInMillis, "something")
    private val Edward = Person("Edward", 24, false, Calendar.getInstance().apply { set(1998, 10, 11) }.timeInMillis, null)
    private val Rosie = Person("Rosie", 16, false, Calendar.getInstance().apply { set(2008, 11, 24) }.timeInMillis, null)

    private val gson = GsonBuilder().serializeNulls().create()
    private val printGson = GsonBuilder().setPrettyPrinting().serializeNulls().create()

    @Test
    fun jsonObject() {
        val json = gson.toJson(Joanna)
        val out = StringWriter()
        logging(json, out)
        assertThenPrint(Joanna, out)
    }

    @Test
    fun jsonArray() {
        val list = listOf(Joanna, Edward, Rosie)
        val json = gson.toJson(list)
        val out = StringWriter()
        logging(json, out)
        assertThenPrint(list, out)
    }

    private fun logging(json: String, out: StringWriter) {
        val jsonReader = JsonReader(StringReader(json))
        val jsonWriter = JsonWriter(out)
        jsonWriter.setIndent("  ")

        HttpJsonLoggingInterceptor().logJson(jsonReader, jsonWriter)
    }

    private fun assertThenPrint(expected: Any, actual: StringWriter) {
        Assert.assertEquals(printGson.toJson(expected), actual.toString())

        println(printGson.toJson(expected))
        println("========= logging ===========")
        println(actual.toString())
    }
}
