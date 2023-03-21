package com.natife.streaming.utils

import android.util.Base64
import java.io.UnsupportedEncodingException

object JWTUtils {
    @Throws(Exception::class)
    fun decoded(JWTEncoded: String): String {
        return try {
            val split = JWTEncoded.split("\\.".toRegex()).toTypedArray()
            getJson(split[1])
        } catch (e: UnsupportedEncodingException) {
            e.message.toString()
        }
    }

    @Throws(UnsupportedEncodingException::class)
    private fun getJson(strEncoded: String): String {
        val decodedBytes: ByteArray = Base64.decode(strEncoded, Base64.URL_SAFE)
        return String(decodedBytes, Charsets.UTF_8)
    }
}