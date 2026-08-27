package com.example.whatsinmyfridge.core.logging

enum class LogLevel { DEBUG, INFO, ERROR }

expect fun platformLog(level: LogLevel, tag: String, message: String, throwable: Throwable?)

object Logger {
    fun d(tag: String, message: String) = platformLog(LogLevel.DEBUG, tag, message, null)
    fun i(tag: String, message: String) = platformLog(LogLevel.INFO, tag, message, null)
    fun e(tag: String, message: String, throwable: Throwable? = null) = platformLog(LogLevel.ERROR, tag, message, throwable)
}
