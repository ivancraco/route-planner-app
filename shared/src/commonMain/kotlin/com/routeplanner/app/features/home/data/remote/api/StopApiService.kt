package com.routeplanner.app.features.home.data.remote.api

import com.routeplanner.app.features.home.data.remote.dto.CreateStopDto
import com.routeplanner.app.features.home.data.remote.dto.UpdateStopDto

interface StopApiService {
    suspend fun createStop(createStopDto: CreateStopDto, routeId: String): Boolean
    suspend fun updateStop(updateStopDto: UpdateStopDto, id: String, routeId: String): Boolean
    suspend fun deleteFromApi(id: String, routeId: String): Boolean
    suspend fun pullFromApi(routeId: String): List<CreateStopDto>
}