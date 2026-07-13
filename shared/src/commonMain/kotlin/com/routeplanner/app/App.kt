package com.routeplanner.app

import androidx.compose.runtime.Composable
import com.routeplanner.app.features.notifier.presentation.navigation.NavigationWrapper
import com.routeplanner.app.ui.RoutePlannerTheme

@Composable
fun App() {
    RoutePlannerTheme {
        NavigationWrapper()
    }
}