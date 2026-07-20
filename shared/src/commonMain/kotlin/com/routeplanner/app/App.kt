package com.routeplanner.app

import androidx.compose.runtime.Composable
import com.routeplanner.app.core.ui.RoutePlannerTheme
import com.routeplanner.app.features.home.presentation.navigation.NavigationWrapper

@Composable
fun App() {
    RoutePlannerTheme {
        NavigationWrapper()
    }
}