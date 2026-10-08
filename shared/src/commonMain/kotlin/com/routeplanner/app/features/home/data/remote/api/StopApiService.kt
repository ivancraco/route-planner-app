package com.routeplanner.app.features.home.data.remote.api

import com.routeplanner.app.features.home.data.remote.dto.CreateStopDto
import com.routeplanner.app.features.home.data.remote.dto.StopOrderDto
import com.routeplanner.app.features.home.data.remote.dto.StopStateDto
import com.routeplanner.app.features.home.data.remote.dto.UpdateStopDto

interface StopApiService {
    suspend fun getStates(): List<StopStateDto>
    suspend fun createStop(createStopDto: CreateStopDto, routeId: String): Boolean
    suspend fun updateStop(updateStopDto: UpdateStopDto, id: String, routeId: String): Boolean
    suspend fun deleteFromApi(id: String, routeId: String): Boolean
    suspend fun pullFromApi(routeId: String): List<CreateStopDto>
    suspend fun reorderInApi(routeId: String, stops: List<StopOrderDto>) : Boolean
}