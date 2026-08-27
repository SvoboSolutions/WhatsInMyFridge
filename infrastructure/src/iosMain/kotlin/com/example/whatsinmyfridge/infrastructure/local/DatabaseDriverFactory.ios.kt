package com.example.whatsinmyfridge.infrastructure.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver =
        NativeSqliteDriver(FridgeDatabase.Schema, "fridge.db")
}
