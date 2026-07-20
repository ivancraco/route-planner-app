package com.routeplanner.app.features.home.data.remote.datasource

import com.routeplanner.app.features.home.data.remote.api.NotifierApiService
import com.routeplanner.app.features.home.data.remote.dto.NotifierRouteSync
import com.routeplanner.app.features.home.domain.model.NotifierRoute

class NotifierRemoteDataSourceImpl(
    private val notifierApiService: NotifierApiService,
): NotifierRemoteDataSource {
    override suspend fun upsertFromApi(
        notifierRouteSync: NotifierRouteSync,
        userId: Long
    ) {
        notifierApiService.upsertFromApi(
            notifierRouteSync = notifierRouteSync,
            userId = userId
        )
    }

    override suspend fun pushToApi(notifierRoute: NotifierRoute) {
        notifierApiService.pushToApi(notifierRoute = notifierRoute)
    }

    override suspend fun deleteFromApi(id: Long) {
        notifierApiService.deleteFromApi(id = id)
    }

    override suspend fun pullFromApi(userId: Long) {
        notifierApiService.pullFromApi(userId = userId)
    }
}