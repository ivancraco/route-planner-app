package com.routeplanner.app.features.notifier.presentation

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.routeplanner.app.features.notifier.data.model.NotifierRouteEntity
import com.routeplanner.app.features.notifier.domain.model.NotifierRoute
import com.routeplanner.app.features.notifier.domain.model.NotifierRouteSummary
import com.routeplanner.app.features.notifier.domain.repository.NotifierRepository
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.util.logging.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

sealed class NotifierRouteState() {
    object Idle: NotifierRouteState()
    object Loading: NotifierRouteState()
    data class Success(val data: List<NotifierRoute>): NotifierRouteState()
    data class Error(val message: String): NotifierRouteState()
}

class NotifierViewModel(
    private val notifierRepository: NotifierRepository
): ViewModel() {
    var state: MutableStateFlow<NotifierRoute?> = MutableStateFlow(null)
        private set
    var summary: MutableStateFlow<List<NotifierRouteSummary>> = MutableStateFlow(listOf())
        private set


    var routes: MutableStateFlow<List<NotifierRoute>> = MutableStateFlow(emptyList())
        private set

    fun createRoute(routeEntity: NotifierRouteEntity) {
        viewModelScope.launch(Dispatchers.Default) {
            val result = notifierRepository.insertRoute(routeEntity)
            state.value = result
        }
    }

    fun getAllRoutes() {
        viewModelScope.launch(Dispatchers.Default) {
            val pepe = notifierRepository.selectAll()
            if (pepe.isSuccess) {
                routes.value = pepe.getOrDefault(emptyList())
            }
        }
    }

    fun getAll() {
        viewModelScope.launch(Dispatchers.Default) {
            notifierRepository.observeRouteSummaries().collectLatest {
                summary.value = it
            }
        }
    }

    fun getById(routeId: Long) {
        viewModelScope.launch(Dispatchers.Default) {
            val result = notifierRepository.selectById(routeId)
            if (result.isSuccess) {
                state.value = result.getOrDefault(null)
            }
        }
    }

    fun updateRouteName(id: Long, name: String) {
        viewModelScope.launch(Dispatchers.Default) {
            notifierRepository.updateName(id, name)
            state.value?.let{
                state.value = it.copy(name = name)
            }
        }
    }

    fun deleteRoute(routeId: Long) {
        viewModelScope.launch(Dispatchers.Default) {
            notifierRepository.softDelete(routeId)
            state.value = null
        }
    }
}