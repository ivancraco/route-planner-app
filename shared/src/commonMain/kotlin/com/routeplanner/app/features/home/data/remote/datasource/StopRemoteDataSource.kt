package com.routeplanner.app.features.home.data.remote.datasource

import com.routeplanner.app.features.home.domain.model.UserStop

interface StopRemoteDataSource {
    suspend fun pushToApi(stop: UserStop, routeId: String, operation: String)
    suspend fun deleteFromApi(id: String, routeId: String)
    suspend fun pullFromApi(routeId: String): List<UserStop>
}