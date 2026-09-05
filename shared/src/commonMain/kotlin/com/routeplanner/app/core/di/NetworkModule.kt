package com.routeplanner.app.core.di

import com.routeplanner.app.features.auth.data.local.SessionLocalDataSource
import com.routeplanner.app.features.home.data.remote.api.RouteApiService
import com.routeplanner.app.features.home.data.remote.api.RouteApiServiceImpl
import com.routeplanner.app.features.home.data.remote.api.StopApiService
import com.routeplanner.app.features.home.data.remote.api.StopApiServiceImpl
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import org.koin.dsl.module
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpHeaders
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named

const val BASE_URL = "http://192.168.0.12:8080/"
val AuthenticatedClient = named("authenticatedClient")
val ExternalClient = named("externalClient")
fun networkModule() = module {
    single<HttpClient>(qualifier = AuthenticatedClient) {
        val session: SessionLocalDataSource = get()
        HttpClient {
            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    }
                )
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        val access = session.getAccessToken()
                        val refresh = session.getRefreshToken()
                        if (access != null) BearerTokens(access, refresh) else null
                    }
                }
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
                sanitizeHeader { header -> header == HttpHeaders.Authorization }
            }
        }
    }

    single<HttpClient>(qualifier = ExternalClient) {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
        }
    }

    single<RouteApiService> { RouteApiServiceImpl(get(AuthenticatedClient)) }
    single<StopApiService> { StopApiServiceImpl(get(AuthenticatedClient)) }
}