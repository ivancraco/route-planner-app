package com.routeplanner.app.core.common.connectivity

import dev.jordond.connectivity.Connectivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface ConnectivityObserver {
    val isConnected: Flow<Boolean>
    suspend fun currentlyConnected(): Boolean
}

class KmpConnectivityObserver : ConnectivityObserver {

    private val connectivity = Connectivity {
        autoStart = true
    }

    override val isConnected: Flow<Boolean> = connectivity.statusUpdates
        .map { it is Connectivity.Status.Connected }

    override suspend fun currentlyConnected(): Boolean {
        return connectivity.status() is Connectivity.Status.Connected
    }
}