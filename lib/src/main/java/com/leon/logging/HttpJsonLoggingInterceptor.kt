package com.leon.logging

import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.io.StringReader
import java.io.StringWriter
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.internal.http.promisesBody
import okhttp3.internal.platform.Platform
import okio.Buffer
import okio.GzipSource

class HttpJsonLoggingInterceptor constructor(
    private val logger: Logger = Logger.DEFAULT
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            throw e
        }

        val responseBody = response.body!!

        if (!response.promisesBody()) {
            // do nothing
        } else if (bodyHasUnknownEncoding(response.headers)) {
            // do nothing
        } else {

            val source = responseBody.source()
            source.request(Long.MAX_VALUE) // Buffer the entire body.
            var buffer = source.buffer

            if ("gzip".equals(response.headers["Content-Encoding"], ignoreCase = true)) {
                GzipSource(buffer.clone()).use { gzippedResponseBody ->
                    buffer = Buffer()
                    buffer.writeAll(gzippedResponseBody)
                }
            }

            val contentType = responseBody.contentType()
            val charset: Charset =
                contentType?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8

            if (!buffer.isProbablyUtf8()) {
                return response
            }
            if (responseBody.contentLength() != 0L) {
                var jsonMessage = "--> JSON <--\n"

                val json = buffer.clone().readString(charset)
                val jsonReader = JsonReader(StringReader(json))
                val stringWriter = StringWriter()
                val jsonWriter = JsonWriter(stringWriter)
                jsonWriter.setIndent("  ")

                try {
                    logJson(jsonReader, jsonWriter)
                    jsonMessage += stringWriter.toString()
                } catch (e: Exception) {
                    logger.log("<-- JSON FAILED: $e")
                    throw e
                } finally {
                    jsonWriter.close()
                }
                jsonMessage += "\n--> JSON END <--"
                logger.log(jsonMessage)
            }
        }

        return response
    }

    interface Logger {
        fun log(message: String)

        companion object {
            /** A [Logger] defaults output appropriate for the current platform. */
            @JvmField
            val DEFAULT: Logger = object : Logger {
                override fun log(message: String) {
                    Platform.get().log(message)
                }
            }
        }
    }

    @Throws(IOException::class)
    fun logJson(reader: JsonReader, writer: JsonWriter) {
        while (reader.hasNext()) {
            when (reader.peek()) {
                JsonToken.END_DOCUMENT -> {
                    return
                }
                JsonToken.BEGIN_OBJECT -> {
                    reader.beginObject()
                    writer.beginObject()
                    logJson(reader, writer)
                    reader.endObject()
                    writer.endObject()
                }
                JsonToken.BEGIN_ARRAY -> {
                    reader.beginArray()
                    writer.beginArray()
                    logJson(reader, writer)
                    reader.endArray()
                    writer.endArray()
                }
                JsonToken.NAME -> {
                    writer.name(reader.nextName())
                }
                JsonToken.STRING -> {
                    writer.value(reader.nextString())
                }
                JsonToken.NUMBER -> {
                    try {
                        writer.value(reader.nextLong())
                    } catch (e: Exception) {
                        writer.value(reader.nextDouble())
                    }
                }
                JsonToken.BOOLEAN -> {
                    writer.value(reader.nextBoolean())
                }
                JsonToken.NULL -> {
                    reader.nextNull()
                    writer.nullValue()
                }
                else -> {
                }
            }
        }
    }

    private fun bodyHasUnknownEncoding(headers: Headers): Boolean {
        val contentEncoding = headers["Content-Encoding"] ?: return false
        return !contentEncoding.equals("identity", ignoreCase = true) &&
            !contentEncoding.equals("gzip", ignoreCase = true)
    }
}
