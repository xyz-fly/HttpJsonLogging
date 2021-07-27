package com.leon.logging

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import org.junit.Assert
import org.junit.Test
import java.io.StringReader
import java.io.StringWriter
import java.util.*

class JsonTest {

    class Person(
        val name: String,
        val age: Int,
        val join: Boolean,
        val birthday: Long,
    )

    val Joanna = Person("Joanna", 18, false, Calendar.getInstance().apply { set(2000, 0, 1) }.timeInMillis)
    val Edward = Person("Edward", 24, false, Calendar.getInstance().apply { set(1998, 10, 11) }.timeInMillis)
    val Rosie = Person("Rosie", 16, false, Calendar.getInstance().apply { set(2008, 11, 24) }.timeInMillis)

    val gson = Gson()
    val printGson = GsonBuilder().setPrettyPrinting().create()

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
