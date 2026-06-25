package com.routeplanner.app.features.common.data.database.adapter

import app.cash.sqldelight.ColumnAdapter
import kotlin.time.Instant

val instantAdapter = object : ColumnAdapter<Instant, String> {
    override fun decode(databaseValue: String) = Instant.parse(databaseValue)
    override fun encode(value: Instant) = value.toString()
}