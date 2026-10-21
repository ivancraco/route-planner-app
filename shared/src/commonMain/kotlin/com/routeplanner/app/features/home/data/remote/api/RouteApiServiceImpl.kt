package com.routeplanner.app.features.home.data.remote.api

import com.routeplanner.app.features.home.data.remote.dto.CreateRouteDto
import com.routeplanner.app.features.home.data.remote.dto.PageResult
import com.routeplanner.app.features.home.data.remote.dto.SupervisorRouteSummaryDto
import com.routeplanner.app.features.home.data.remote.dto.UpdateRouteDto
import com.routeplanner.app.features.home.domain.model.SupervisorRouteSummary
import com.routeplanner.app.features.home.domain.model.UserRoute
import com.routeplanner.app.features.home.domain.model.toCreateRouteDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class RouteApiServiceImpl(
    private val httpClient: HttpClient
): RouteApiService {
    override suspend fun upsertFromApi(
        userRoute: UserRoute,
        userId: Long
    ) {
        return httpClient.put("") {
            setBody(userRoute.toCreateRouteDto())
        }.body()
    }

    override suspend fun createRoute(createRoute: CreateRouteDto): Boolean {
        return httpClient.post("routes") {
            contentType(ContentType.Application.Json)
            setBody(createRoute)
        }.body<Boolean>()
    }

    override suspend fun updateRoute(route: UpdateRouteDto, id: String): Boolean {
        return httpClient.put("routes/$id") {
            contentType(ContentType.Application.Json)
            setBody(route)
        }.body<Boolean>()
    }

    override suspend fun deleteFromApi(id: String): Boolean {
        return httpClient.delete("routes/$id")
            .body<Boolean>()
    }

    override suspend fun pullFromApi(userId: Long): List<CreateRouteDto> {
        return httpClient.get("routes") {
            //parameter("userId", userId)
        }.body<List<CreateRouteDto>>()
    }

    override suspend fun getPage(
        username: String?,
        page: Int,
        pageSize: Int
    ): PageResult<SupervisorRouteSummaryDto> {
        return httpClient.get("routes/all") {
            parameter("username", username)
            parameter("page", page)
            parameter("pageSize", pageSize)
        }.body()
    }
}