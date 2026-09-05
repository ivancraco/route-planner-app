package com.routeplanner.app.features.home.data.remote.datasource

import com.routeplanner.app.core.utils.SyncOperation
import com.routeplanner.app.features.home.data.remote.api.StopApiService
import com.routeplanner.app.features.home.data.remote.mapper.toDomain
import com.routeplanner.app.features.home.domain.model.UserStop
import com.routeplanner.app.features.home.domain.model.toCreateStopDto
import com.routeplanner.app.features.home.domain.model.toUpdateStopDto

class StopRemoteDataSourceImpl(
    private val stopApiService: StopApiService
) : StopRemoteDataSource {
    override suspend fun pushToApi(
        stop: UserStop,
        routeId: String,
        operation: String
    ) {
        when (operation) {
            SyncOperation.INSERT -> stopApiService.createStop(
                createStopDto = stop.toCreateStopDto(),
                routeId = routeId
            )

            SyncOperation.UPDATE -> stopApiService.updateStop(
                updateStopDto = stop.toUpdateStopDto(),
                id = stop.id,
                routeId = routeId
            )
        }
    }

    override suspend fun deleteFromApi(id: String, routeId: String) {
        stopApiService.deleteFromApi(id, routeId)
    }

    override suspend fun pullFromApi(routeId: String): List<UserStop> {
        return stopApiService.pullFromApi(routeId).map { it.toDomain(routeId) }
    }
}