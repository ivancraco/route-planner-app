package com.routeplanner.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.routeplanner.app.features.notifier.presentation.RoutePlannerScreen
import com.routeplanner.app.features.notifier.presentation.previewRoute
import com.routeplanner.app.ui.RoutePlannerTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            RoutePlannerTheme {
                App()
            }
        }
    }
}