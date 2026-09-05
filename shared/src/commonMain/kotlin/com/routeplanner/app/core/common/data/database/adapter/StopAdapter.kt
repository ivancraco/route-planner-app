package com.routeplanner.app.core.common.data.database.adapter

import app.cash.sqldelight.ColumnAdapter
import com.routeplanner.app.features.home.domain.model.UserStop
import kotlinx.serialization.json.Json

val stopsAdapter = object : ColumnAdapter<List<UserStop>, String> {
    override fun decode(databaseValue: String) = Json.decodeFromString<List<UserStop>>(databaseValue)
    override fun encode(value: List<UserStop>) = Json.encodeToString(value)
}