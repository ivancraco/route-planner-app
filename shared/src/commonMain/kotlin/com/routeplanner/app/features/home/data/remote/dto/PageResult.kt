package com.routeplanner.app.features.home.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PageResult<T>(
    val items: List<T>,
    val page: Int,
    val pageSize: Int,
    val totalItems: Long,
    val totalPages: Int
)
