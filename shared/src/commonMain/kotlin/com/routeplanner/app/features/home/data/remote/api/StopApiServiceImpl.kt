package com.routeplanner.app.features.home.data.remote.api

import com.routeplanner.app.core.di.BASE_URL
import com.routeplanner.app.features.home.data.remote.dto.CreateStopDto
import com.routeplanner.app.features.home.data.remote.dto.StopOrderDto
import com.routeplanner.app.features.home.data.remote.dto.StopStateDto
import com.routeplanner.app.features.home.data.remote.dto.UpdateStopDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class StopApiServiceImpl(
    private val httpClient: HttpClient
): StopApiService {
    override suspend fun getStates(): List<StopStateDto> {
        return httpClient.get("${BASE_URL}stop-states").body()
    }

    override suspend fun createStop(
        createStopDto: CreateStopDto,
        routeId: String
    ): Boolean {
        println("--id: ${createStopDto.id}")
        return httpClient.post("${BASE_URL}routes/$routeId/stops") {
            contentType(ContentType.Application.Json)
            setBody(createStopDto)
        }.body<Boolean>()
    }

    override suspend fun updateStop(
        updateStopDto: UpdateStopDto,
        id: String,
        routeId: String
    ): Boolean {
        return httpClient.put("${BASE_URL}routes/$routeId/stops/$id") {
            contentType(ContentType.Application.Json)
            setBody(updateStopDto)
        }.body<Boolean>()
    }

    override suspend fun deleteFromApi(id: String, routeId: String): Boolean {
        return httpClient.delete("${BASE_URL}routes/$routeId/stops/$id")
            .body<Boolean> ()
    }

    override suspend fun pullFromApi(routeId: String): List<CreateStopDto> {
        return httpClient.get("${BASE_URL}routes/$routeId/stops")
            .body<List<CreateStopDto>>()
    }

    override suspend fun reorderInApi(
        routeId: String,
        stops: List<StopOrderDto>
    ): Boolean {
        return httpClient.put(urlString = "${BASE_URL}routes/$routeId/stops/reorder") {
            contentType(ContentType.Application.Json)
            setBody(stops)
        }.body<Boolean>()
    }
}