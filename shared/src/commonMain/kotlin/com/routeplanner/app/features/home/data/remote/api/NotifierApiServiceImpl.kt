package com.routeplanner.app.features.home.data.remote.api

import com.routeplanner.app.features.home.data.remote.dto.NotifierRouteSync
import com.routeplanner.app.features.home.domain.model.NotifierRoute
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.put

class NotifierApiServiceImpl(
    private val client: HttpClient
): NotifierApiService {
    override suspend fun upsertFromApi(
        notifierRouteSync: NotifierRouteSync,
        userId: Long
    ) {
        client.get {

        }
    }

    override suspend fun pushToApi(notifierRoute: NotifierRoute) {
        client.put {

        }
    }

    override suspend fun deleteFromApi(id: Long) {
        client.put {

        }
    }

    override suspend fun pullFromApi(userId: Long) {
        client.get {

        }
    }
}