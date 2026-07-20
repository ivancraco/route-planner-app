package com.routeplanner.app.core.common.data.database.adapter

import app.cash.sqldelight.ColumnAdapter
import com.routeplanner.app.features.home.domain.model.NotifierStop
import kotlinx.serialization.json.Json

val stopsAdapter = object : ColumnAdapter<List<NotifierStop>, String> {
    override fun decode(databaseValue: String) = Json.decodeFromString<List<NotifierStop>>(databaseValue)
    override fun encode(value: List<NotifierStop>) = Json.encodeToString(value)
}