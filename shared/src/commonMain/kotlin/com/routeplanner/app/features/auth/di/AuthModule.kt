package com.routeplanner.app.features.auth.di

import com.routeplanner.app.core.di.AuthenticatedClient
import com.routeplanner.app.features.auth.data.AuthRepositoryImpl
import com.routeplanner.app.features.auth.data.local.SessionLocalDataSource
import com.routeplanner.app.features.auth.data.remote.AuthApi
import com.routeplanner.app.features.auth.domain.repository.AuthRepository
import com.routeplanner.app.features.auth.presentation.AuthViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Se asume que `HttpClient` (Ktor) y `Settings` (multiplatform-settings) ya
 * están provistos por tu NetworkModule / core module correspondiente, y por
 * eso acá simplemente se resuelven con get().
 *
 * No olvides incluir este módulo al armar tu lista de módulos de Koin, ej:
 *   startKoin { modules(networkModule, coreModule, authModule, ...) }
 */
fun authModule() = module {
    factory { AuthApi(httpClient = get(AuthenticatedClient)) }

    single { SessionLocalDataSource(settings = get()) }

    single<AuthRepository> {
        AuthRepositoryImpl(
            authApi = get(),
            sessionLocalDataSource = get()
        )
    }

    viewModel { AuthViewModel(authRepository = get()) }
}
