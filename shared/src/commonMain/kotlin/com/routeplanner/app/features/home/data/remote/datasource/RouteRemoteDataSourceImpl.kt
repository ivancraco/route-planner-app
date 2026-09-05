package com.routeplanner.app.features.home.data.remote.datasource

import com.routeplanner.app.features.home.data.remote.api.RouteApiService
import com.routeplanner.app.features.home.data.remote.mapper.toDomain
import com.routeplanner.app.features.home.domain.model.UserRoute
import com.routeplanner.app.features.home.domain.model.toCreateRouteDto
import com.routeplanner.app.features.home.domain.model.toUpdateRouteDto

class RouteRemoteDataSourceImpl(
    private val routeApiService: RouteApiService,
): RouteRemoteDataSource {
    override suspend fun upsertFromApi(
        userRoute: UserRoute,
        userId: Long
    ) {
        routeApiService.upsertFromApi(
            userRoute = userRoute,
            userId = userId
        )
    }

    override suspend fun createRoute(userRoute: UserRoute) {
        routeApiService.createRoute(userRoute.toCreateRouteDto())
    }

    override suspend fun updateRoute(userRoute: UserRoute) {
        val id = userRoute.id
        routeApiService.updateRoute(userRoute.toUpdateRouteDto(), id)
    }

    override suspend fun deleteFromApi(id: String) {
        routeApiService.deleteFromApi(id = id)
    }

    override suspend fun pullFromApi(userId: Long): List<UserRoute> {
        val result = routeApiService.pullFromApi(userId = userId)
        return result.map { it.toDomain() }
    }
}