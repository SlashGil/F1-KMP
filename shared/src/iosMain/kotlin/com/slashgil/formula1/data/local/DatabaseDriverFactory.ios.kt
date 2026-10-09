package com.slashgil.formula1.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.slashgil.formula1.db.F1Database

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(F1Database.Schema, "f1_database.db")
    }
}