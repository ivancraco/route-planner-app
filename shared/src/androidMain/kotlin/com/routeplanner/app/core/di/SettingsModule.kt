package com.routeplanner.app.core.di

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val settingsModule = module {
    single<Settings> {
        val prefs = androidContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        SharedPreferencesSettings(prefs)
    }
}