package com.routeplanner.app.features.home.data.remote.api

import com.routeplanner.app.core.di.BASE_URL
import com.routeplanner.app.features.home.data.remote.dto.CreateRouteDto
import com.routeplanner.app.features.home.data.remote.dto.UpdateRouteDto
import com.routeplanner.app.features.home.domain.model.UserRoute
import com.routeplanner.app.features.home.domain.model.toCreateRouteDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
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
        println("--r id: ${createRoute.id}")
        return httpClient.post("${BASE_URL}routes") {
            contentType(ContentType.Application.Json)
            setBody(createRoute)
        }.body<Boolean>()
    }

    override suspend fun updateRoute(route: UpdateRouteDto, id: String): Boolean {
        return httpClient.put("${BASE_URL}routes/$id") {
            contentType(ContentType.Application.Json)
            setBody(route)
        }.body<Boolean>()
    }

    override suspend fun deleteFromApi(id: String): Boolean {
        return httpClient.delete("${BASE_URL}routes/$id")
            .body<Boolean>()
    }

    override suspend fun pullFromApi(userId: Long): List<CreateRouteDto> {
        return httpClient.get("${BASE_URL}/routes") {
            //parameter("userId", userId)
        }.body<List<CreateRouteDto>>()
    }
}