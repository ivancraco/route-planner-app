package com.routeplanner.app.core.common.connectivity

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    val isConnected: Flow<Boolean>
    suspend fun currentlyConnected(): Boolean
}