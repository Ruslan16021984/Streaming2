package com.natife.streaming.ext

// Allows you to get a value from a list from a single Map

fun <K, V> Map<K, V>.getSingleValue(): V? {
    return this.values.toList().getOrNull(0)
}