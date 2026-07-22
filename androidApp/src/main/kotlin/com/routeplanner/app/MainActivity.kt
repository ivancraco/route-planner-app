package com.routeplanner.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.routeplanner.app.core.common.onAndroidForeground
import com.routeplanner.app.core.utils.SyncManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }

    override fun onResume() {
        super.onResume()
        onAndroidForeground()
    }
}