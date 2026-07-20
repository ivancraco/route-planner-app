package com.routeplanner.app.features.home.data.remote.datasource

import com.routeplanner.app.features.home.data.remote.dto.NotifierRouteSync
import com.routeplanner.app.features.home.domain.model.NotifierRoute

interface NotifierRemoteDataSource {
    suspend fun upsertFromApi(notifierRouteSync: NotifierRouteSync, userId: Long)
    suspend fun pushToApi(notifierRoute: NotifierRoute)
    suspend fun deleteFromApi(id: Long)
    suspend fun pullFromApi(userId: Long)
}