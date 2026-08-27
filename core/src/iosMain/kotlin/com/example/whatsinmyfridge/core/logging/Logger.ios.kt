package com.example.whatsinmyfridge.core.logging

import platform.Foundation.NSLog

actual fun platformLog(level: LogLevel, tag: String, message: String, throwable: Throwable?) {
    NSLog("[%s/%s] %s%s", level.name, tag, message, throwable?.let { " - $it" } ?: "")
}
